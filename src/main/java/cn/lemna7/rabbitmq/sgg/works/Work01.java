package cn.lemna7.rabbitmq.sgg.works;

import cn.lemna7.rabbitmq.sgg.utils.RabbitMQUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author admin
 * @Description 消费在手动应答时不丢失，并放回队列中重新消费
 */
public class Work01 {

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            try {
                Thread.sleep(1000 * 30);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(new String(message.getBody()));

            /**
             * 确认收到一条或多条消息
             * 1. 接收到的消息标签
             * 2. true：确认所有消息，false：本地消息标签的消息
             */
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
        };
        CancelCallback cancelCallback = consumerTag -> {
            System.out.println("message canceled");
        };

        boolean autoAck = false;
        channel.basicConsume("lemna7_queue", autoAck, deliverCallback, cancelCallback);
    }
}
