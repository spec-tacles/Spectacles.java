package com.spectacles.gateway.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.spectacles.entities.gateway.GatewayBot;
import com.spectacles.gateway.Cluster;
import com.spectacles.gateway.Shard;
import com.spectacles.gateway.ws.ShardWebSocketClient;
import com.spectacles.gateway.ws.events.ShardWebSocketMessageEvent;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutorService;

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
     * The http client for the shard
     */
    private OkHttpClient client = new OkHttpClient();

    /**
     * The cluster this shard is in
     */
    private Cluster cluster = null;

    /**
     * The cached gateway (if exists)
     */
    private GatewayBot gateway;

    /**
     * The Discord token of the bot account
     */
    private String token;

    /**
     * The shard count
     */
    private int shardCount;

    /**
     * Event bus to handle events
     */
    @SuppressWarnings("")
    private EventBus eventBus = new EventBus();

    /**
     * The WebSocket client
     */
    private ShardWebSocketClient wsclient = null;

    /**
     * The pool for the async operations, it is listening and can be added callbacks to while still being totally safe
     */
    private ListeningExecutorService pool;

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
        this.pool = MoreExecutors.newDirectExecutorService();
    }

    /**
     * The constructor is given a cluster object (of the cluster that manages it probably), the shard id and a pool
     *
     * @param cluster the cluster
     * @param id      the shard id
     * @param service the thread pool to use for async operations
     */
    public ShardImpl(Cluster cluster, int id, ListeningExecutorService service) {
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
        this.pool = MoreExecutors.newDirectExecutorService();
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
        this.pool = MoreExecutors.listeningDecorator(service);
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
    public String getToken() {
        return token;
    }

    private ListenableFuture<?> connectNoCheck() {
        return pool.submit(() -> {
            log.debug(logFormat("Connecting to the Discord Gateway..."));
            try {
                wsclient = new ShardWebSocketClient(URI.create(gateway.getUrl()), this);
                wsclient.connectBlocking();
            } catch (InterruptedException e) {
                log.error(logFormat("Couldn't connect to the Discord Gateway"));
            }
        });
    }

    @Override
    public ListenableFuture<?> connectAsync() {
        if (wsclient != null && (wsclient.getConnection() == null || wsclient.getConnection().isClosed())) {
            log.debug(logFormat("Old WebSocket client exists, disposing it..."));
            wsclient.close();
        }

        if (gateway != null) {
            return connectNoCheck();
        } else {
            return Futures.transform(GatewayBot.getAsync(this, client), (gateway) -> {
                this.gateway = gateway;
                return connectNoCheck();
            }, pool);
        }
    }

    @Override
    public ListenableFuture<?> disconnectAsync(int closeCode, String reason) {
        return pool.submit(() -> wsclient.closeConnection(closeCode, reason));
    }

    @Override
    public ListenableFuture<?> sendAsync() {
        return null;
    }

    @Override
    public void addListener(Object listener) {
        this.eventBus.register(listener);
    }

    @Override
    public void removeListener(Object listener) {
        this.eventBus.unregister(listener);
    }


    @Override
    public void close() throws IOException {
        try {
            wsclient.closeBlocking();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    private class GatewayHandler {
        @Subscribe
        public void onEvent(ShardWebSocketMessageEvent event) {
            try {

                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(event.getMessage());
                int opcode = node.get("op").asInt();
                switch (opcode) {
                    default:
                        log.debug(logFormat(String.valueOf(opcode)));
                }
            } catch (IOException e) {
                log.debug(logFormat("Couldn't deserialize the message sent by the gateway."));
            }

        }
    }

}
