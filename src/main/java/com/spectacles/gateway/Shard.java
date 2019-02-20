package com.spectacles.gateway;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.Future;

/**
 * The Interface of the Shard
 */
public interface Shard extends Closeable {

    /**
     * Connects the shard to the Discord Gateway
     */
    Future<Void> connect();

    /**
     * Disconnects the shard from the Discord Gateway
     */
    Future<Void> disconnect();

    /**
     * Sends a message to the Discord Gateway
     */
    Future<Void> send();

    /**
     * Add event listeners
     * @param eventListeners the event listeners to add
     */
    void addListeners(ShardEventListener... eventListeners);

    /**
     * Remove event listeners
     * @param eventListeners the event listeners to remove
     */
    void removeListeners(ShardEventListener... eventListeners);

    /**
     * Get event listeners
     * @return the event listeners
     */
    List<ShardEventListener> getListeners();

}
