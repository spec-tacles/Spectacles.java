package com.spectacles.gateway.impl;

import com.spectacles.gateway.Cluster;
import com.spectacles.gateway.Shard;
import com.spectacles.gateway.ShardEventListener;
import com.spectacles.gateway.ws.ShardWebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class ShardImpl implements Shard {

    /**
     * The logger
     */
    private Logger log = LoggerFactory.getLogger(getClass());

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
     * The WebSocket client
     */
    private ShardWebSocketClient wsclient = new ShardWebSocketClient(URI.create("wss://gateway.discord.gg/?v=6&encoding=json"), this);

    /**
     * The pool for the async operations
     */
    private ExecutorService pool;

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
        this.pool = ForkJoinPool.commonPool();
    }

    /**
     * The constructor is given a cluster object (of the cluster that manages it probably), the shard id and a pool
     *
     * @param cluster the cluster
     * @param id      the shard id
     * @param service the thread pool to use for async operations
     */
    public ShardImpl(Cluster cluster, int id, ExecutorService service) {
        this.cluster = cluster;
        this.id = id;
        this.token = cluster.getToken();
        this.shardCount = cluster.getShardCount();
        this.pool = service;
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
        this.pool = ForkJoinPool.commonPool();
    }

    /**
     * The constructor is given a token, an id, and the shard count to send to discord on identify
     *
     * @param token      the Discord token
     * @param id         the shard id
     * @param shardCount the shard count
     * @param service    the thread pool to use for async operations
     */
    public ShardImpl(String token, int id, int shardCount, ExecutorService service) {
        this.token = token;
        this.id = id;
        this.shardCount = shardCount;
        this.pool = service;
    }

    /**
     * Adds shard id to the message logged
     *
     * @param message the message to log
     * @return the message with the shard id prepended
     */
    private String logFormat(String message) {
        return String.format("[Shard %d] %s", id, message);
    }

    @Override
    public boolean inCluster() {
        return cluster != null;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Future<?> connect() {
        return pool.submit(() -> {
            if (wsclient.getConnection() == null || wsclient.getConnection().isClosed()) {
                log.debug(logFormat("Old WebSocket client exists, disposing it..."));
                wsclient.close();
            }

            log.debug("Connecting to the Discord Gateway...");
            wsclient = new ShardWebSocketClient(URI.create("wss://gateway.discord.gg/?v=6&encoding=json"), this);
            try {
                wsclient.connectBlocking();
            } catch (InterruptedException e) {

            }
        });
    }

    @Override
    public Future<?> disconnect(int closeCode, String reason) {
        return pool.submit(() -> wsclient.closeConnection(closeCode, reason));
    }

    @Override
    public Future<?> send() {
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
        try {
            wsclient.closeBlocking();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }
}
