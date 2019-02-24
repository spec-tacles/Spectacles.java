package com.spectacles.gateway;

import java.io.Closeable;

public interface Cluster extends Closeable {

    /**
     * Gets the Discord token
     * @return the token
     */
    String getToken();

    /**
     * Gets the shard count to send to Discord on Identify
     * @return the shard count
     */
    int getShardCount();

}
