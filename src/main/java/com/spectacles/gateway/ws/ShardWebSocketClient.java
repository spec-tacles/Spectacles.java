package com.spectacles.gateway.ws;

import com.spectacles.gateway.Shard;
import com.spectacles.gateway.ws.events.ShardWebSocketCloseEvent;
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

public class ShardWebSocketClient extends WebSocketClient {

    private Logger log = LoggerFactory.getLogger(getClass());

    private Shard shard;

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

    private String logFormat(String message) {
        return String.format("[Shard %d] %s", shard.getId(), message);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.debug(logFormat("Connected to WebSocket"));

    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onMessage(ByteBuffer bytes) {

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
