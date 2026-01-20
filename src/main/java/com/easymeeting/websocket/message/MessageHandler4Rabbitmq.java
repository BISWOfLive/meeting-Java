package com.easymeeting.websocket.message;

import com.easymeeting.entity.constants.Constants;
import com.easymeeting.entity.dto.MessageSendDto;
import com.easymeeting.exception.BusinessException;
import com.easymeeting.utils.JsonUtils;
import com.easymeeting.websocket.ChannelContextUtils;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Component
@ConditionalOnProperty(name = Constants.MESSAGEING_HANDLE_CHANNEL_KEY,havingValue = Constants.MESSAGEING_HANDLE_CHANNEL_RABBITMQ)
@Slf4j
public class MessageHandler4Rabbitmq implements MessageHandler{

    private static final String EXCHANGE_NAEM = "fanout_exchange";

    private static final Integer MAX_RETRYTIMES = 3;

    private static final String RETRY_COUNT_KEY = "retryCount";

    @Resource
    private ChannelContextUtils channelContextUtils;
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;

    @Value("${rabbitmq.host:}")
    private String redisHost;

    @Value("${rabbitmq.port:}")
    private Integer redisPort;

    /**
     * 监听RabbitMQ消息队列，接收并处理消息
     * 该方法建立RabbitMQ连接，创建fanout交换机和队列，设置消息处理回调函数
     * 实现消息的接收、处理和确认机制，当消息处理失败时进行重试
     */
    @Override
    public void listenMessage() {
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAEM, BuiltinExchangeType.FANOUT);
            String queueName = channel.queueDeclare().getQueue();
            // 将队列绑定到交换机上，路由键为空字符串
            channel.queueBind(queueName,EXCHANGE_NAEM,"");
            Boolean autoAck = false;
            // 定义消息投递回调函数，用于处理接收到的消息
            DeliverCallback deliverCallback = (consumerTag,dellivery)->{
                try {
                    String message = new String(dellivery.getBody(), "UTF-8");
                    log.info("rabbitmq收到消息:{}",message);
                    // 将消息转换为MessageSendDto对象并发送到WebSocket客户端
                    channelContextUtils.sendMessage(JsonUtils.convertJson2Obj(message,MessageSendDto.class));
                    // 手动确认消息已处理完成，false表示不批量确认
                    channel.basicAck(dellivery.getEnvelope().getDeliveryTag(),false);
                }catch (Exception e){
                    log.info("处理信息失败",e);
                    // 调用失败消息处理方法，实现消息重试机制
                    handleFaileMessage(channel,dellivery,queueName);
                }
            };
            // 开始消费队列消息，参数：队列名、是否自动确认、消息处理回调、消费者取消回调
            channel.basicConsume(queueName,autoAck,deliverCallback,consumerTag -> {
            });
        }catch (Exception e){
            log.error("rabbitmq监听消息失败",e);
        }
    }

    // 方法定义：处理失败的消息，带有三个参数和一个异常声明
    private void handleFaileMessage(Channel channel, Delivery dellivery, String queueName) throws IOException {
        // 获取消息的头部信息（包含一些元数据），这些信息可能包含重试次数等控制信息
        Map<String, Object> headers = dellivery.getProperties().getHeaders();
        // 如果消息头为空（第一次处理消息时通常为空），则创建一个新的HashMap存储头部信息
        if (headers == null){
            headers = new HashMap<>();
        }
        // 初始化重试计数器为0
        Integer retryCount = 0;
        // 检查消息头中是否已包含重试次数键值对
        if (headers.containsKey(RETRY_COUNT_KEY)){
            // 从消息头中获取当前的重试次数
            retryCount = (Integer) headers.get(RETRY_COUNT_KEY);
        }
        // 判断当前重试次数是否小于最大重试次数减1（即是否还可以重试）
        if (retryCount < MAX_RETRYTIMES - 1){
            // 更新重试次数
            headers.put(RETRY_COUNT_KEY,retryCount + 1);
            // 构建新的AMQP消息属性，包含更新后的消息头
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().headers(headers).build();
            // 将消息重新发布到同一个队列，等待下一次处理
            channel.basicPublish("",queueName,properties, dellivery.getBody());
            // 确认原消息已处理完成（虽然实际是重新发布，但原消息的处理流程已结束）
            channel.basicAck(dellivery.getEnvelope().getDeliveryTag(),false);
        }else {
            // 如果重试次数已达上限
            // 记录日志，说明消息已达到最大重试次数，将被丢弃
            log.info("超过最大重试次数,放弃处理");
            // 拒绝原消息，不重新入队（false参数表示不重新入队）
            channel.basicReject(dellivery.getEnvelope().getDeliveryTag(),false);
        }
    }


    @Override
    public void sendMessage(MessageSendDto messageSendDto) {
        try(Connection connection = factory.newConnection(); Channel channel = connection.createChannel()){
            channel.exchangeDeclare(EXCHANGE_NAEM,BuiltinExchangeType.FANOUT);
            String message = "";
            channel.basicPublish(EXCHANGE_NAEM,"",null,message.getBytes());
        }catch (Exception e){
            log.error("rabbitmq发送消息失败");
        }
    }

    @PreDestroy
    public void destroy() throws IOException,TimeoutException{
        if (channel != null && channel.isOpen()){
            channel.close();
        }
        if (connection != null && connection.isOpen()){
            connection.close();
        }
    }
}
