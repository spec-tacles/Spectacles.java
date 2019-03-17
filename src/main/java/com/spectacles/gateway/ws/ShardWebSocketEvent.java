package com.spectacles.gateway.ws;

import com.spectacles.entities.event.Event;

public abstract class ShardWebSocketEvent implements Event {

    /**
     * The WebSocketClient
     */
    private ShardWebSocketClient webSocketClient;

    private String event;

    private Object data;

    public ShardWebSocketEvent(String event, Object data, ShardWebSocketClient webSocketClient) {
        this.event = event;
        this.data = data;
        this.webSocketClient = webSocketClient;
    }

    public ShardWebSocketClient getWebSocketClient() {
        return webSocketClient;
    }

    @Override
    public String getEvent() {
        return event;
    }

    @Override
    public Object getData() {
        return data;
    }
}
