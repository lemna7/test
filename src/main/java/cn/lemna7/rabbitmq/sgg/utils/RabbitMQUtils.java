package cn.lemna7.rabbitmq.sgg.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


/**
 * @author admin
 * @Description RabbitMQ Utils Class
 */
public class RabbitMQUtils {

    /**
     * get channel
     * @return
     * @throws IOException
     * @throws TimeoutException
     */
    public static Channel getChannel() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("111.229.153.118");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin123456");

        Connection connection = connectionFactory.newConnection();
        return connection.createChannel();
    }
}
