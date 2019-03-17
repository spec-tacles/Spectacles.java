package com.spectacles.entities.event;

/**
 * The event invoked from receiving a message
 */
public interface Event {
    /**
     * The event name
     * @return event name
     */
    String getEvent();

    /**
     * The data UTF-8 encoded
     * @return data
     */
    Object getData();
}
