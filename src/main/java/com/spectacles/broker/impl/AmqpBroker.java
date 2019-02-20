package com.spectacles.broker.impl;

import com.rabbitmq.client.*;
import com.spectacles.broker.BrokerEventListener;
import com.spectacles.broker.BrokerReceivedEvent;
import com.spectacles.broker.Broker;
import com.spectacles.entities.EventListener;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;

/**
 * An implementation of the broker using RabbitMQ (AMQP)
 */
public class AmqpBroker implements Broker {

    /**
     * The event listeners
     */
    private final LinkedList<BrokerEventListener> listeners = new LinkedList<>();

    /**
     * The consumerTags of events
     */
    private final HashMap<String, String> consumerTags = new HashMap<>();

    /**
     * Consumer tag to channel map
     */
    private final HashMap<String, Channel> consumerChannels = new HashMap<>();

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
     * The channel pool
     * There should be a channel for every thread to ensure thread safety
     */
    private ThreadLocal<Channel> channelPool = new ThreadLocal<>();

    private Channel getSetChannel() throws IOException {
        if (connection == null) {
            throw new IllegalArgumentException("There is no ongoing connection! Are you sure you called connect()?");
        }

        if (channelPool.get() == null) {
            Channel channel = connection.createChannel();
            channelPool.set(channel);
            channel.exchangeDeclare(group, "direct", false, false, new HashMap<>());
        }
        return channelPool.get();
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
     * @throws URISyntaxException being thrown from {@link ConnectionFactory#setUri(URI)}
     * @throws NoSuchAlgorithmException being thrown from {@link ConnectionFactory#setUri(URI)}
     * @throws KeyManagementException being thrown from {@link ConnectionFactory#setUri(URI)}
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
    public void addListeners(BrokerEventListener... eventListeners) {
        listeners.addAll(Arrays.asList(eventListeners));
    }

    @Override
    public void removeListeners(BrokerEventListener... eventListeners) {
        listeners.removeAll(Arrays.asList(eventListeners));
    }

    @Override
    public List<BrokerEventListener> getListeners() {
        return listeners;
    }

    @Override
    public Future<Void> publish(String event, byte[] data) {
        CompletableFuture<Void> f = new CompletableFuture<>();
        pool.submit(()->{
            try {
                getSetChannel().basicPublish(group, event, new AMQP.BasicProperties(), data);
                f.complete(null);
            } catch (Exception e) {
                f.completeExceptionally(e);
            }
        });
        return f;
    }

    @Override
    public Future<Void> subscribe(String... events) {
       return CompletableFuture.allOf((CompletableFuture[]) Arrays
               .stream(events)
               .map((event) -> {
                   CompletableFuture<Void> future = new CompletableFuture<>();
                   pool.submit(() -> {
                       try {
                           String qname = String.format("%s%s%s", group, subgroup, event);
                           Channel channel = getSetChannel();
                           channel.queueDeclare(qname, false, false, false, new HashMap<>());
                           channel.queueBind(qname, group, event);
                           String consumer = channel.basicConsume(qname, false, new DefaultConsumer(channel) {
                               @Override
                               public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                                   for (BrokerEventListener listener : listeners) {
                                       listener.onEvent(new BrokerReceivedEvent(event, body));
                                   }
                                   channel.basicAck(envelope.getDeliveryTag(), false);
                               }
                           });
                           consumerTags.put(event, consumer);
                           consumerChannels.put(consumer, channel);
                           future.complete(null);
                       } catch (IOException e) {
                           future.completeExceptionally(e);
                       }
                   });
                   return future;
               }).toArray()
       );
    }

    @Override
    public Future<Void> unsubscribe(String... events) {
        return CompletableFuture.allOf((CompletableFuture[]) Arrays
                .stream(events)
                .map((event)-> {
                    CompletableFuture<Void> f = new CompletableFuture<>();
                    pool.submit(()->{
                        try {
                            if (consumerTags.containsKey(event)) {
                                String consumer = consumerTags.get(event);
                                Channel channel = consumerChannels.get(consumer);
                                channel.basicCancel(consumerTags.get(event));
                                consumerChannels.remove(consumerTags.get(event));
                                f.complete(null);
                            } else {
                                f.completeExceptionally(new UnsupportedOperationException("This event hasn't been subscribed to!"));
                            }
                        } catch (IOException e) {
                            f.completeExceptionally(e);
                        }
                    });
                    return f;
                })
                .toArray());
    }

    @Override
    public void close() throws IOException {
        if (connection != null) {
            connection.close();
        }
    }
}
