package com.spectacles.gateway.ws.events;

import com.spectacles.gateway.ws.ShardWebSocketClient;
import com.spectacles.gateway.ws.ShardWebSocketEvent;

/**
 * The event that is being called when the websocket closes
 */
public class ShardWebSocketCloseEvent extends ShardWebSocketEvent {

    /**
     * The response code
     */
    private int code;

    /**
     * The reason of shutdown
     */
    private String reason;

    /**
     * Whether it was closed remotely
     */
    private boolean remote;

    /**
     *
     * @param webSocketClient the websocketclient
     * @param code the response code
     * @param reason the reason
     * @param remote whether it was closed remotely
     */
    public ShardWebSocketCloseEvent(ShardWebSocketClient webSocketClient, int code, String reason, boolean remote) {
        super("ws-close", reason, webSocketClient);
    }

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    public boolean isRemote() {
        return remote;
    }
}
