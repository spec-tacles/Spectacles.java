package com.spectacles.gateway.ws.events;

import com.spectacles.gateway.ws.ShardWebSocketClient;
import com.spectacles.gateway.ws.ShardWebSocketEvent;

/**
 * The event invoked on errors
 */
public class ShardWebSocketErrorEvent extends ShardWebSocketEvent {

    /**
     * The Exception occured
     */
    private Exception exception;

    public ShardWebSocketErrorEvent(Exception e, ShardWebSocketClient webSocketClient) {
        super("ws-error", e.getMessage(), webSocketClient);
        this.exception = e;
    }

    /**
     * Get the exception occured
     *
     * @return exception
     */
    public Exception getException() {
        return exception;
    }
}
