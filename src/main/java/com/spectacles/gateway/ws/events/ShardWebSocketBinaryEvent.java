package com.spectacles.gateway.ws.events;

import com.spectacles.gateway.ws.ShardWebSocketClient;
import com.spectacles.gateway.ws.ShardWebSocketEvent;

import java.nio.ByteBuffer;

public class ShardWebSocketBinaryEvent extends ShardWebSocketEvent {

    /**
     * The message received
     */
    private byte[] message;

    public ShardWebSocketBinaryEvent(ByteBuffer message, ShardWebSocketClient webSocketClient) {
        super("ws-binary", message, webSocketClient);
        this.message = message.array();
    }

    /**
     * Gets the message received
     *
     * @return message
     */
    public byte[] getMessage() {
        return message;
    }

}
