package com.spectacles.gateway.impl;

import com.spectacles.gateway.Cluster;
import com.spectacles.gateway.Shard;
import com.spectacles.gateway.ShardEventListener;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

public class ShardImpl implements Shard {

    /**
     * The shard's id
     */
    private int id;

    /**
     * The cluster this shard is in
     */
    private Cluster cluster = null;

    /**
     * The Discord token of the bot account
     */
    private String token;

    /**
     * The shard count
     */
    private int shardCount;

    /**
     * The constructor is given a cluster object (of the cluster that manages it probably) and the shard id
     * @param cluster the cluster
     * @param id the shard id
     */
    public ShardImpl(Cluster cluster, int id) {
        this.cluster = cluster;
        this.id = id;
        this.token = cluster.getToken();
        this.shardCount = cluster.getShardCount();
    }

    /**
     * The constructor is given a token, an id, and the shard count to send to discord on identify
     * @param token the Discord token
     * @param id the shard id
     * @param shardCount the shard count
     */
    public ShardImpl(String token, int id, int shardCount) {
        this.token = token;
        this.id = id;
        this.shardCount = shardCount;
    }

    @Override
    public boolean inCluster() {
        return cluster != null;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public Future<Void> connect() {
        return null;
    }

    @Override
    public Future<Void> disconnect() {
        return null;
    }

    @Override
    public Future<Void> send() {
        return null;
    }

    @Override
    public void addListeners(ShardEventListener... eventListeners) {

    }

    @Override
    public void removeListeners(ShardEventListener... eventListeners) {

    }

    @Override
    public List<ShardEventListener> getListeners() {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
