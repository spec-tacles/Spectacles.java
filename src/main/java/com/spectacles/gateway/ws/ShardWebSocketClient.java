package com.spectacles.gateway.ws;

import com.google.common.eventbus.EventBus;
import com.spectacles.gateway.Shard;
import com.spectacles.gateway.ws.events.ShardWebSocketBinaryEvent;
import com.spectacles.gateway.ws.events.ShardWebSocketCloseEvent;
import com.spectacles.gateway.ws.events.ShardWebSocketMessageEvent;
import com.spectacles.gateway.ws.events.ShardWebSocketOpenEvent;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * The WebSocket client that manages the shard's connection
 */
public class ShardWebSocketClient extends WebSocketClient {

    /**
     * The logger
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * The shard associated with this client
     */
    private Shard shard;

    /**
     * The listener bus
     */
    @SuppressWarnings("While it is considered beta, it's because it might change in the future.")
    private final EventBus eventBus;

    /**
     * Build the websocket using only host
     * @param serverUri the uri
     * @param shard the shard
     */
    public ShardWebSocketClient(URI serverUri, Shard shard) {
        super(serverUri);
        this.shard = shard;
        eventBus = new EventBus();
    }

    /**
     * Build the websocket using the host and additional http headers
     * @param serverUri the uri
     * @param httpHeaders the http headers
     * @param shard the shard
     */
    public ShardWebSocketClient(URI serverUri, Map<String, String> httpHeaders, Shard shard) {
        super(serverUri, httpHeaders);
        this.shard = shard;
        eventBus = new EventBus();
    }

    /**
     * Add a listener to the listener list
     * @param listener the listener to add
     */
    public void addListener(Object listener) {
        this.eventBus.register(listener);
    }

    /**
     * Remove a listener from the listener list
     * @param listener the listener to remove
     */
    public void removeListener(Object listener) {
        this.eventBus.unregister(listener);
    }

    /**
     * Called on any event
     * @param event the event called
     */
    private void onEvent(ShardWebSocketEvent event) {
        eventBus.post(event);
    }

    /**
     * Adds shard id to the message logged
     * @param message the message to log
     * @return the message with the shard id prepended
     */
    private String logFormat(String message) {
        return String.format("[Shard %d] %s", shard.getId(), message);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.debug(logFormat("Connected to WebSocket"));
        onEvent(new ShardWebSocketOpenEvent(handshakedata, this));
    }

    @Override
    public void onMessage(String message) {
        onEvent(new ShardWebSocketMessageEvent(message, this));
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        onEvent(new ShardWebSocketBinaryEvent(bytes, this));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        onEvent(new ShardWebSocketCloseEvent(this, code, reason, remote));
    }

    @Override
    public void onError(Exception ex) {
        log.warn(logFormat("While connecting to the WebSocket the client has encountered an exception:\n" + ex.getMessage()));

    }
}
