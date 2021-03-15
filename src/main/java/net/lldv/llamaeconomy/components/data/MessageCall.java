package net.lldv.llamaeconomy.components.data;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.SneakyThrows;
import net.lldv.llamaeconomy.LlamaEconomy;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.BiConsumer;

public class MessageCall {

    private final LlamaEconomy plugin;
    private Connection connection;
    private Channel channel;

    @SneakyThrows
    public MessageCall(final LlamaEconomy plugin) {
        this.plugin = plugin;

        final ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        this.connection = connectionFactory.newConnection("Economy/Server");

        this.channel = this.connection.createChannel();
    }

    @SneakyThrows
    public void createCall(final BiConsumer<CallData, String[]> callBack, final CallData callData, final String... args) {
        if (!this.channel.isOpen() || !this.connection.isOpen()) {
            final ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost("localhost");
            this.connection = connectionFactory.newConnection("Economy/Server");

            this.channel = this.connection.createChannel();
        }
        final String corrId = UUID.randomUUID().toString();
        final StringBuilder callBuilder = new StringBuilder(callData.name().toLowerCase() + "//");
        for (String arg : args) {
            callBuilder.append(arg).append("//");
        }
        final String call = callBuilder.substring(0, callBuilder.length() - 2);

        //this.plugin.getLogger().info(call);

        final String replyQueueName = this.channel.queueDeclare().getQueue();
        final AMQP.BasicProperties properties = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        this.channel.basicPublish("", "economy", properties, call.getBytes(StandardCharsets.UTF_8));

        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

        final String tag = this.channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response.offer(new String(delivery.getBody(), StandardCharsets.UTF_8));
            }
        }, consumerTag -> {
        });

        final String result = response.take();
        this.channel.basicCancel(tag);
        callBack.accept(CallData.valueOf(result.toUpperCase().split("//")[0]), result.split("//"));
    }

}
