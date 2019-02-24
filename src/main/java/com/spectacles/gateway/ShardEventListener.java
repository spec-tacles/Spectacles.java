package com.spectacles.gateway;

import com.spectacles.entities.event.EventListener;

public interface ShardEventListener extends EventListener<ShardEvent> {

    void onEvent(ShardEvent event);
}
