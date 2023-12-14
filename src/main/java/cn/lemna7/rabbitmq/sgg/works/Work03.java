package cn.lemna7.rabbitmq.sgg.works;

import cn.lemna7.rabbitmq.sgg.utils.RabbitMQUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author admin
 * @Description 死信队列 消费者 C2
 */
public class Work03 {
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        // 消费成功回调
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println(consumerTag + ": " + new String(message.getBody(), "UTF-8"));
        };
        // 消费消息
        channel.basicConsume("dead-queue", true, deliverCallback, (consumerTag) -> {
        });
    }
}
