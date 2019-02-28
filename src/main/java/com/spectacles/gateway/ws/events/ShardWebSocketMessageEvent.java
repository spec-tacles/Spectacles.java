package com.spectacles.gateway.ws.events;

import com.spectacles.gateway.ws.ShardWebSocketClient;
import com.spectacles.gateway.ws.ShardWebSocketEvent;

/**
 * The event invoked on message
 */
public class ShardWebSocketMessageEvent extends ShardWebSocketEvent {

    /**
     * The message received
     */
    private String message;

    public ShardWebSocketMessageEvent(String message, ShardWebSocketClient webSocketClient) {
        super("ws-message", message, webSocketClient);
        this.message = message;
    }

    /**
     * Gets the message received
     *
     * @return message
     */
    public String getMessage() {
        return message;
    }
}
