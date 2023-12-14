package cn.lemna7.rabbitmq.sgg.tasks;


import cn.lemna7.rabbitmq.sgg.utils.RabbitMQUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author admin
 * @Description 死信队列 生产者
 */
public class Task02 {
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        // 消费者已经声明就不用声明了
        // channel.exchangeDeclare("normal-exchange", BuiltinExchangeType.DIRECT, false, false, null);

        /**
         * 发布消息并设置消息的属性
         * 消息过期时间为10seconds TTL，如果10s中没有人接收，过了10s后就进入死信队列
         */
        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("10000").build();
        for (int i = 0; i < 10; i++) {
            channel.basicPublish("normal-exchange", "zhangsan", properties, "message".getBytes());
            System.out.println("message send success");
        }
    }
}
