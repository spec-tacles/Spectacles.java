package com.spectacles.gateway;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.Closeable;

/**
 * The Interface of the Shard
 */
public interface Shard extends Closeable {

    /**
     * Shards don't have to be in a cluster, they can be constructed on their own
     * @return Whether or not this shard is in a cluster
     */
    boolean inCluster();

    /**
     * Gets the shard id
     * @return the shard id
     */
    int getId();

    /**
     * Gets the bot's token
     *
     * @return the token
     */
    String getToken();

    /**
     * Connects the shard to the Discord Gateway
     */
    ListenableFuture<?> connectAsync();

    /**
     * Disconnects the shard from the Discord Gateway
     * @param closeCode the close code to send
     * @param reason the reason of closing
     */
    ListenableFuture<?> disconnectAsync(int closeCode, String reason);

    /**
     * Sends a message to the Discord Gateway
     */
    ListenableFuture<?> sendAsync();

    /**
     * Add event listeners
     * @param listener the event listeners to add
     */
    void addListener(Object listener);

    /**
     * Remove event listeners
     * @param listener the event listeners to remove
     */
    void removeListener(Object listener);

}
