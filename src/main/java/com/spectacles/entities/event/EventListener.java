package com.spectacles.entities.event;

/**
 * A basic listener for events
 */
public interface EventListener<T extends Event> {
    /**
     * A function to execute on event invoked
     * @param event the event invoked
     */
    void onEvent(T event);
}
