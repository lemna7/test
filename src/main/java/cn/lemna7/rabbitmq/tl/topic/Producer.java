package cn.lemna7.rabbitmq.tl.topic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author lemna7
 * 生产者
 * 交换机为主题类型
 */
public class Producer {
    /**
     * 交换机名
     */
    private static final String EXCHANGE_NAME = "lemna7_topic_exchange";

    /**
     * 队列
     */
    private static final String QUEUE_NAME_1 = "lemna7_topic_queue_1";
    private static final String QUEUE_NAME_2 = "lemna7_topic_queue_2";
    private static final String QUEUE_NAME_3 = "lemna7_topic_queue_3";
    private static final String QUEUE_NAME_4 = "lemna7_topic_queue_4";

    /**
     * 路由键
     */
    private static String key1 = "key1.key2.key3.*";
    private static String key2 = "key1.#";
    private static String key3 = "*.key2.*.key4";
    private static String key4 = "#.key3.key4";

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

        /**
         * 声明交换机
         * 1. 交换机名
         * 2. 交换机类型：direct、topic、fanout、headers，headers 类型的交换机的性能很差，不建议使用
         * 3. 是否持久化，重启后依然存在
         * 4. 交换机在没有与队列绑定时，是否删除
         * 5. 交换机的其它属性
         */
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC, false, true, null);

        /**
         * 声明队列
         * 1. 队列名
         * 2. 是否持久化
         * 3. 是否为私有队列，仅限此链接可用
         * 4. 队列没有消费者订阅时自动删除
         * 5. 队列的其它属性，比如声明为死信队列、磁盘队列
         */
        channel.queueDeclare(QUEUE_NAME_1, false, false, false, null);
        channel.queueDeclare(QUEUE_NAME_2, false, false, false, null);
        channel.queueDeclare(QUEUE_NAME_3, false, false, false, null);
        channel.queueDeclare(QUEUE_NAME_4, false, false, false, null);

        /**
         * 将队列绑定到交换机
         * 四个队列，交换器通过路由键模式匹配，规则是通过 . 来分割为多个部分，# 匹配0个或多个部分，* 匹配一部分
         * 1. 队列名称
         * 2. 交换机名称
         * 3. 路由键，在直连模式下为队列名称
         */
        channel.queueBind(QUEUE_NAME_1, EXCHANGE_NAME, key1);
        channel.queueBind(QUEUE_NAME_2, EXCHANGE_NAME, key2);
        channel.queueBind(QUEUE_NAME_3, EXCHANGE_NAME, key3);
        channel.queueBind(QUEUE_NAME_4, EXCHANGE_NAME, key4);

        /**
         * 发布消息
         * 1. 发送到哪个交换机
         * 2. 路由键，在直连模式下可以为队列名称
         * 3. 消息的其它参数
         * 4. 消息体
         */
        String message = "topic_message";
        channel.basicPublish(EXCHANGE_NAME, "key1", null, message.getBytes());

        System.out.println("消息发送成功~~~~~~~~~~~~~~~~");

        // 关闭信道和连接
        channel.close();
        connection.close();
    }
}
