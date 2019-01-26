package com.spectacles.broker.impl;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.spectacles.broker.Broker;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.*;

/**
 * An implementation of the broker using RabbitMQ (AMQP)
 */
public class AmqpBroker implements Broker {

    /**
     * The thread pool to use for Async operations
     */
    private final ExecutorService pool;

    public ExecutorService getPool() {
        return pool;
    }


    /**
     * The AMQP exchange of this broker
     */
    private final String group;

    public String getGroup() {
        return group;
    }

    /**
     * The subgroup of this broker.
     * Subgroups are used to split data between groups of the same data
     * It is primarily an additional identifier of the queue name
     */
    private final String subgroup;

    public String getSubgroup() {
        return subgroup;
    }

    /**
     * The AMQP connection this broker is connected to
     */
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    /**
     * The AMQP channel this broker is connected to
     */
    private Channel channel;

    public Channel getChannel() {
        return channel;
    }

    public AmqpBroker(final String group, final String subgroup, final ExecutorService pool) {
        this.group = group;
        this.subgroup = subgroup;
        this.pool = pool == null ? ForkJoinPool.commonPool() : pool;
    }

    /**
     * Connect to the RabbitMQ instance using the factory
     * @param factory the connection factory
     * @return A future
     */
    public Future<Void> connect(final ConnectionFactory factory) {
        CompletableFuture<Void> f = new CompletableFuture<>();
        pool.submit(() -> {
            try {
                connection = factory.newConnection();
                channel = connection.createChannel();
                channel.exchangeDeclare(group, "direct", true, false, new HashMap<>());
                f.complete(null);
            } catch (Exception e) {
                f.completeExceptionally(e);
            }
        });
        return f;
    }

    /**
     * Connect with a connection uri
     * @param uri the connection uri
     * @return A future
     * @throws URISyntaxException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public Future<Void> connect(final URI uri) throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(uri);
        return connect(factory);
    }

                                /**
     * Connects to the RabbitMQ instance
     * @param host the uri of the server
     * @return A future
     */
    public Future<Void> connect(final String host) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        return connect(factory);
    }

    /**
     * Connects to the RabbitMQ instance
     * @param host the uri of the server
     * @param port the port of the server
     * @return A future
     */
    public Future<Void> connect(final String host, final int port) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        return connect(factory);
    }

    /**
     * Connects to the RabbitMQ instance
     * @param host the uri of the server
     * @param port the port of the server
     * @param username the username
     * @param password the password
     * @return A future
     */
    public Future<Void> connect(final String host, final int port, final String username, final String password) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        return connect(factory);
    }

    @Override
    public Future<Void> publish(String event, byte[] data) {
        CompletableFuture<Void> f = new CompletableFuture<>();
        pool.submit(()->{
            try {
                channel.basicPublish(group, event, new AMQP.BasicProperties(), data);
                f.complete(null);
            } catch (Exception e) {
                f.completeExceptionally(e);
            }
        });
        return f;
    }

    @Override
    public Future<Void> subscribe(String... events) {
        CompletableFuture<Void> f = new CompletableFuture<>();
        pool.submit(()->{
            for (String event : events) {
                try {
                    String qname = String.format("%s%s%s", group, subgroup, event);
                    channel.queueDeclare(qname, true, false, false, new HashMap<>());
                    channel.queueBind(qname, group, event);
                    // WIP Need to add event listeners
                } catch (IOException e) {
                    f.completeExceptionally(e);
                    break;
                }

            }
        });
        return f;
    }

    @Override
    public Future<Void> unsubscribe(String... events) {
        return null;
    }
}
