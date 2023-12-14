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
 * @Description 死信队列 消费者 C1
 */
public class Work02 {
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        // 声明普通交换机和死信交换机
        channel.exchangeDeclare("normal-exchange", BuiltinExchangeType.DIRECT, false, false, null);
        channel.exchangeDeclare("dead-exchange", BuiltinExchangeType.DIRECT, false, false, null);
        // 声明普通队列和死信队列
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "dead-exchange");
        arguments.put("x-dead-letter-routing-key", "lisi");
        // 设置队列的最大长度，超过最大长度后转到死信队列
        // arguments.put("x-max-length", 6);
        channel.queueDeclare("normal-queue", false, false, false, arguments);
        channel.queueDeclare("dead-queue", false, false, false, null);
        // 队列绑定交换机
        channel.queueBind("normal-queue", "normal-exchange", "zhangsan");
        channel.queueBind("dead-queue", "dead-exchange", "lisi");
        // 消费成功回调
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("tag: " + message.getEnvelope().getDeliveryTag());
            if (message.getEnvelope().getDeliveryTag() == 5) {
                channel.basicReject(message.getEnvelope().getDeliveryTag(), false);
            } else {
                System.out.println(consumerTag + ": " + new String(message.getBody(), "UTF-8"));
                channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            }
        };
        boolean autoAsk = false;
        // 消费消息
        channel.basicConsume("normal-queue", autoAsk, deliverCallback, (consumerTag) -> {
        });
    }
}
