package net.audiocall.client.util;

public interface ListChangeEventListener<T> {

    void onListChanged(ListChangeEvent<T> event);
}
