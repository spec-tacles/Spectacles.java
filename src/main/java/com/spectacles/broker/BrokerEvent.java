package com.spectacles.broker;

import com.spectacles.entities.event.Event;

import java.nio.charset.StandardCharsets;

public class BrokerEvent implements Event {

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

    public BrokerEvent(String event, byte[] data) {
        this.event = event;
        this.data = new String(data, StandardCharsets.UTF_8);
    }
}
