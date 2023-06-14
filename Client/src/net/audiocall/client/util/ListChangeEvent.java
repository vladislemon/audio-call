package net.audiocall.client.util;

import java.util.List;
import java.util.Objects;

public class ListChangeEvent<T> {

    private final List<T> source;
    private final int index;
    private final T oldValue;
    private final T newValue;

    public ListChangeEvent(List<T> source, int index, T oldValue, T newValue) {
        this.source = source;
        this.index = index;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public List<T> getSource() {
        return source;
    }

    public int getIndex() {
        return index;
    }

    public T getOldValue() {
        return oldValue;
    }

    public T getNewValue() {
        return newValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListChangeEvent<?> that = (ListChangeEvent<?>) o;

        if (index != that.index) return false;
        if (!Objects.equals(oldValue, that.oldValue)) return false;
        return Objects.equals(newValue, that.newValue);
    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + (oldValue != null ? oldValue.hashCode() : 0);
        result = 31 * result + (newValue != null ? newValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ListChangeEvent{" +
                "index=" + index +
                ", oldValue=" + oldValue +
                ", newValue=" + newValue +
                '}';
    }
}
