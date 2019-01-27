package com.spectacles.broker;

import com.rabbitmq.client.BasicProperties;

/**
 * The event invoked from receiving a message
 */
public interface ReceivedEvent {
    /**
     * The event name
     * @return event name
     */
    String getEvent();

    /**
     * The data UTF-8 encoded
     * @return data
     */
    String getData();
}
