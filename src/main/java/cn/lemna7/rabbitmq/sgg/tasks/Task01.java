package cn.lemna7.rabbitmq.sgg.tasks;

import cn.lemna7.rabbitmq.sgg.utils.RabbitMQUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;

import java.io.IOException;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;

/**
 * @author admin
 * @Description 生产者 确认发布
 */
public class Task01 {
    private static final int MESSAGE_COUNT = 1000;

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        // publicMessageConfirmSingle();
        // publicMessageConfirmBatch();
        publicMessageConfirmAsync();
    }

    /**
     * 单个确认发布
     * @throws IOException
     * @throws TimeoutException
     */
    public static void publicMessageConfirmSingle() throws IOException, TimeoutException, InterruptedException {
        Channel channel = RabbitMQUtils.getChannel();
        // 开启发布确认
        channel.confirmSelect();
        channel.queueDeclare("task01_01", false, false, false, null);
        long start = System.currentTimeMillis();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            channel.basicPublish("", "task01_01", null, ("Message" + i).getBytes());
            // 等待确认
            channel.waitForConfirms();
        }
        long end = System.currentTimeMillis();
        System.out.println("单个确认发布花费时间：" + (end - start));
    }

    /**
     * 批量确认发布
     * @throws IOException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public static void publicMessageConfirmBatch() throws IOException, InterruptedException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        // 开启发布确认
        channel.confirmSelect();
        channel.queueDeclare("task01_02", false, false, false, null);
        long start = System.currentTimeMillis();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            channel.basicPublish("", "task01_02", null, ("Message" + i).getBytes());
        }
        // 批量等待确认
        channel.waitForConfirms();
        long end = System.currentTimeMillis();
        System.out.println("批量确认发布花费时间：" + (end - start));
    }

    /**
     * 异步批量确认发布
     * @throws IOException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public static void publicMessageConfirmAsync() throws IOException, InterruptedException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        // 开启发布确认
        channel.confirmSelect();
        channel.queueDeclare("task01_03", false, false, false, null);

        /**
         * 线程安全有序的哈希表，适用于高并发的情况
         * 1. 将序列号和消息关联
         * 2. 通过序列号删除条目
         * 3. 支持并发访问
         */
        ConcurrentSkipListMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();

        /**
         * 确认收到消息回调
         */
        ConfirmCallback ackCallback = (deliveryTag, multiple) -> {
            if (multiple) {
                // 返回的是小于等于当前序列号的未确认消息
                ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(deliveryTag, true);
                confirmed.clear();
            } else {
                System.out.println("要被移除的消息：" + deliveryTag);
                outstandingConfirms.remove(deliveryTag);
            }
            System.out.println("outstandingConfirms count 2:" + outstandingConfirms.size());
        };

        /**
         * 未收到消息的回调
         */
        ConfirmCallback nackCallback = (deliveryTag, multiple) -> {
            String message = outstandingConfirms.get(deliveryTag);
            System.out.println("发布的消息"+message+"未被确认，序列号"+deliveryTag);
        };

        /**
         * 异步批量确认的监听器
         * 1. 确认收到消息回调
         * 2. 未收到消息的回调
         */
        channel.addConfirmListener(ackCallback, nackCallback);

        long start = System.currentTimeMillis();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            /**
             * channel.getNextPublishSeqNo() 获取下一个消息的序列号
             * 消息号与消息体关联
             * 全部都是未确认的消息
             */
            outstandingConfirms.put(channel.getNextPublishSeqNo(), ("Message" + i));
            System.out.println(" seqNo:" + channel.getNextPublishSeqNo());
            channel.basicPublish("", "task01_03", null, ("Message" + i).getBytes());
        }
        long end = System.currentTimeMillis();
        System.out.println("异步批量确认发布花费时间：" + (end - start));
        System.out.println("outstandingConfirms count 1:" + outstandingConfirms.size());
    }
}
