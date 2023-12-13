package cn.lemna7.rabbitmq.tl;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author lemna7
 * 消费者
 */
public class Consumer {
    /**
     * 队列名
     */
    private static final String QUEUE_NAME = "lemna7_queue";

    /**
     * 服务端 ip address
     */
    private static final String HOST = "111.229.153.118";

    /**
     * 服务端 port
     */
    private static final int PORT = 5672;

    /**
     * 账号
     */
    private static final String USERNAME = "admin";

    /**
     * 密码
     */
    private static final String PASSWORD = "admin123456";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接工厂并配置工厂参数
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(HOST);
        connectionFactory.setPort(PORT);
        connectionFactory.setUsername(USERNAME);
        connectionFactory.setPassword(PASSWORD);

        // 创建连接和信道
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();


        // 处理消息回调
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println(new String(message.getBody(), StandardCharsets.UTF_8));
        };

        // 取消处理消息回调
        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println("Consumer was canceled");
        };

        /**
         * 消费消息
         * 1. 消费哪个队列
         * 2. 是否自动应答消息
         * 3. 处理消息回调
         * 3. 取消处理消息回调
         */
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);

        // 关闭信道和连接
        channel.close();
        connection.close();
    }
}
