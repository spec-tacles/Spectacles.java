package com.spectacles.gateway;

import com.spectacles.entities.EventListener;

public interface ShardEventListener extends EventListener<ShardReceivedEvent> {

    void onEvent(ShardReceivedEvent event);
}
