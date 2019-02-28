package com.spectacles.gateway.ws.events;

import com.spectacles.gateway.ws.ShardWebSocketClient;
import com.spectacles.gateway.ws.ShardWebSocketEvent;
import org.java_websocket.handshake.Handshakedata;

/**
 * The event invoked on connection open
 */
public class ShardWebSocketOpenEvent extends ShardWebSocketEvent {

    /**
     * The data of the tcp handshake
     */
    private Handshakedata handshakedata;

    public ShardWebSocketOpenEvent(Handshakedata handshakedata, ShardWebSocketClient webSocketClient) {
        super("ws-open", new String(handshakedata.getContent()), webSocketClient);
        this.handshakedata = handshakedata;
    }

    /**
     * Get the handshake data
     *
     * @return handshake data
     */
    public Handshakedata getHandshakedata() {
        return handshakedata;
    }
}
