package com.spectacles.gateway.ws;

import com.google.common.util.concurrent.RateLimiter;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    private RateLimiter rateLimiter = new SmoothRateLImite

    /**
     * The listener list
     */
    private List<ShardWebSocketEventListener> listeners = new ArrayList<>();

    /**
     * Build the websocket using only host
     * @param serverUri the uri
     * @param shard the shard
     */
    public ShardWebSocketClient(URI serverUri, Shard shard) {
        super(serverUri);
        this.shard = shard;
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
    }

    /**
     * Add listeners to the listener list
     * @param listeners the listeners to add
     */
    public void addListeners(ShardWebSocketEventListener... listeners) {
        this.listeners.addAll(Arrays.asList(listeners));
    }

    /**
     * Remove listeners from the listener list
     * @param listeners the listeners to remove
     */
    public void removeListeners(ShardWebSocketEventListener... listeners) {
        this.listeners.removeAll(Arrays.asList(listeners));
    }

    /**
     * Called on any event
     * @param event the event called
     */
    private void onEvent(ShardWebSocketEvent event) {
        for (ShardWebSocketEventListener listener : listeners) {
            listener.onEvent(event);
        }
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
        log.debug(logFormat("Received a message: " + message));
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
        log.warn(logFormat("While connecting to the WebSocket the client has encountered an exception"), ex.getCause());

    }
}
