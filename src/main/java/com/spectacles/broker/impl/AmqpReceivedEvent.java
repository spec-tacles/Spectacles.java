package com.spectacles.broker.impl;

import com.spectacles.broker.ReceivedEvent;

import java.nio.charset.StandardCharsets;

public class AmqpReceivedEvent implements ReceivedEvent {

    String event;
    String data;

    @Override
    public String getEvent() {
        return event;
    }

    @Override
    public String getData() {
        return data;
    }

    public AmqpReceivedEvent(String event, byte[] data) {
        this.event = event;
        this.data = new String(data, StandardCharsets.UTF_8);
    }
}
