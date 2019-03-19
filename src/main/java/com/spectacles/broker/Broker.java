package com.spectacles.broker;

import java.io.Closeable;
import java.util.concurrent.Future;

/**
 * The Basic Broker Interface
 * The broker is used to communicate between the nodes
 */
public interface Broker extends Closeable {

    /**
     * Add event listeners
     * @param eventListener the event listeners to add
     */
    void addListener(Object eventListener);

    /**
     * Remove event listeners
     * @param eventListener the event listeners to remove
     */
    void removeListener(Object eventListener);

    /**
     * Publishes a message of an event asynchronously
     * @param event The event name
     * @param data The message data
     * @return A future
     */
    Future<Void> publish(String event, byte[] data);

    /**
     * Subscribes to events
     * @param events the events to subscribe to
     * @return A future
     */
    Future<Void> subscribe(String... events);

    /**
     * Unsubscribes from events
     * @param events the events to unsubscribe to
     * @return A future
     */
    Future<Void> unsubscribe(String... events);
}
