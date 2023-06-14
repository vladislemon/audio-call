package net.audiocall.client.util;

import java.util.AbstractList;
import java.util.LinkedList;
import java.util.List;

public class ObservableList<T> extends AbstractList<T> {

    private final List<T> internalList;
    private final List<ListChangeEventListener<T>> changeListeners;

    public ObservableList(List<T> list) {
        this.internalList = list;
        this.changeListeners = new LinkedList<>();
    }

    private void onChange(int index, T oldValue, T newValue) {
        changeListeners.forEach((l) -> {
            l.onListChanged(new ListChangeEvent<>(this, index, oldValue, newValue));
        });
    }

    public void addChangeListener(ListChangeEventListener<T> changeListener) {
        this.changeListeners.add(changeListener);
    }

    public boolean removeChangeListener(ListChangeEventListener<T> changeListener) {
        return this.changeListeners.remove(changeListener);
    }

    @Override
    public T get(int index) {
        return internalList.get(index);
    }

    @Override
    public T set(int index, T element) {
        T oldValue = internalList.set(index, element);
        onChange(index, oldValue, element);
        return oldValue;
    }

    @Override
    public void add(int index, T element) {
        internalList.add(index, element);
        onChange(index, null, element);
    }

    @Override
    public T remove(int index) {
        T oldValue = internalList.remove(index);
        onChange(index, oldValue, null);
        return oldValue;
    }

    @Override
    public int size() {
        return internalList.size();
    }
}
