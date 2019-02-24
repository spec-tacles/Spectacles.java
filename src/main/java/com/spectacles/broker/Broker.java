package com.spectacles.broker;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.Future;

/**
 * The Basic Broker Interface
 * The broker is used to communicate between the nodes
 */
public interface Broker extends Closeable {

    /**
     * Add event listeners
     * @param eventListeners the event listeners to add
     */
    void addListeners(BrokerEventListener... eventListeners);

    /**
     * Remove event listeners
     * @param eventListeners the event listeners to remove
     */
    void removeListeners(BrokerEventListener... eventListeners);

    /**
     * Get event listeners
     * @return the event listeners
     */
    List<BrokerEventListener> getListeners();

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
