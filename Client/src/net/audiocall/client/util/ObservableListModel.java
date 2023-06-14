package net.audiocall.client.util;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class ObservableListModel<T> extends AbstractListModel<T> {

    private final ObservableList<T> internalList;

    public ObservableListModel(ObservableList<T> list) {
        this.internalList = list;
        this.internalList.addChangeListener(this::listChangeListener);
    }

    private void listChangeListener(ListChangeEvent<T> event) {
        if(SwingUtilities.isEventDispatchThread()) {
            onInternalListChange(event);
        } else {
            try {
                SwingUtilities.invokeAndWait(() -> onInternalListChange(event));
            } catch (InterruptedException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private void onInternalListChange(ListChangeEvent<T> event) {
        if (event.getOldValue() == null && event.getNewValue() != null) {
            fireIntervalAdded(this, event.getIndex(), event.getIndex());
        } else {
            if (event.getNewValue() == null) {
                fireIntervalRemoved(this, event.getIndex(), event.getIndex());
            } else {
                fireContentsChanged(this, event.getIndex(), event.getIndex());
            }
        }
    }

    public void destroy() {
        internalList.removeChangeListener(this::onInternalListChange);
    }

    @Override
    public int getSize() {
        return internalList.size();
    }

    @Override
    public T getElementAt(int index) {
        return internalList.get(index);
    }
}
