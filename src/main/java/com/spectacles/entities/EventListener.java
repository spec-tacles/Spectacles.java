package com.spectacles.entities;

/**
 * A basic listener for events
 */
public interface EventListener<T extends ReceivedEvent> {
    /**
     * A function to execute on event invoked
     * @param event the event invoked
     */
    void onEvent(T event);
}
