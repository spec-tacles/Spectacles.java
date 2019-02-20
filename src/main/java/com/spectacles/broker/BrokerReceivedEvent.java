package com.spectacles.broker;

import com.spectacles.entities.ReceivedEvent;

import java.nio.charset.StandardCharsets;

public class BrokerReceivedEvent implements ReceivedEvent {

    private String event;
    private String data;

    @Override
    public String getEvent() {
        return event;
    }

    @Override
    public String getData() {
        return data;
    }

    public BrokerReceivedEvent(String event, byte[] data) {
        this.event = event;
        this.data = new String(data, StandardCharsets.UTF_8);
    }
}
