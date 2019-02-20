package com.spectacles.gateway;

import com.spectacles.entities.ReceivedEvent;

/**
 * The events received by the shard
 */
public class ShardReceivedEvent implements ReceivedEvent {

    /**
     * The shard's id
     */
    private int id;

    /**
     * The event name
     */
    private String event;

    /**
     * The data
     */
    private String data;



    @Override
    public String getEvent() {
        return event;
    }

    @Override
    public String getData() {
        return data;
    }

    /**
     * Returns the ID of the shard
     * @return id
     */
    public int getId() {
        return id;
    }
}
