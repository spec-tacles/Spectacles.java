package com.spectacles.broker.impl;

import java.util.concurrent.ExecutorService;

/**
 * Simplifies the way to build a broker
 */
public class AmqpBrokerBuilder {
    private String group = "";
    private String subgroup = "";
    private ExecutorService pool = null;

    public String getGroup() {
        return group;
    }

    public String getSubgroup() {
        return subgroup;
    }

    public ExecutorService getPool() {
        return pool;
    }

    public AmqpBrokerBuilder setGroup(String group) {
        this.group = group;
        return this;
    }

    public AmqpBrokerBuilder setSubgroup(String subgroup) {
        this.subgroup = subgroup;
        return this;
    }

    public AmqpBrokerBuilder setPool(ExecutorService pool) {
        this.pool = pool;
        return this;
    }

    public AmqpBroker build() {
        return new AmqpBroker(group, subgroup, pool);
    }
}
