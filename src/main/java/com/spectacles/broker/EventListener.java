package com.spectacles.broker;

/**
 * A basic listener for events
 */
public interface EventListener {
    /**
     * A function to execute on event invoked
     * @param event the event invoked
     */
    void onEvent(ReceivedEvent event);
}
