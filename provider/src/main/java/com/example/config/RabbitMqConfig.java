package com.example.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SerializerMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class RabbitMqConfig {
    /**
     * 库存交换机
     */

    public static final String STORY_EXCHANGE = "STORY_EXCHANGE";

    /**
     * 订单交换机
     */
    public static final String ORDER_EXCHANGE = "ORDER_EXCHANGE";

    /**
     * 库存队列
     */
    public static final String STORY_QUEUE = "STORY_QUEUE";

    /**
     * 订单队列
     */
    public static final String ORDER_QUEUE = "ORDER_QUEUE";

    /**
     * 库存路由键
     */
    public static final String STORY_ROUTING_KEY = "STORY_ROUTING_KEY";

    /**
     * 订单路由键
     */
    public static final String ORDER_ROUTING_KEY = "ORDER_ROUTING_KEY";

    public static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    public static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    public static final String EXCHANGE_TOPICS_INFORM="exchange_topics_inform";
    public static final String ROUTINGKEY_EMAIL="inform.#.email.#";
    public static final String ROUTINGKEY_SMS="inform.#.sms.#";



    public static final String CANAL_EXCHANGE="CANAL_EXCHANGE";
    public static final String CANAL_QUEUE="CANAL_QUEUE";
    public static final String CANAL_ROUTING_KEY="CANAL_ROUTING_KEY";
    /**
     *
     * @param connectionFactory
     * @return SimpleRabbitListenerContainerFactory
     */
    @Bean(name = "rabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //消费数量
        factory.setPrefetchCount(50);
        return factory;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 创建库存交换机
     * @return
     */
    @Bean
    public Exchange getStoryExchange() {
        return ExchangeBuilder.directExchange(STORY_EXCHANGE).durable(true).build();
    }

    /**
     * 创建库存队列
     * @return
     */
    @Bean
    public Queue getStoryQueue() {
        return new Queue(STORY_QUEUE,true);
    }

    /**
     * 库存交换机和库存队列绑定
     * @return
     */
    @Bean
    public Binding bindStory() {
        return BindingBuilder.bind(getStoryQueue()).to(getStoryExchange()).with(STORY_ROUTING_KEY).noargs();
    }

    /**
     * 创建订单队列
     * @return
     */
    @Bean
    public Queue getOrderQueue() {
        return new Queue(ORDER_QUEUE);
    }

    /**
     * 创建订单交换机
     * @return
     */
    @Bean
    public Exchange getOrderExchange() {
        return ExchangeBuilder.directExchange(ORDER_EXCHANGE).durable(true).build();
    }

    /**
     * 订单队列与订单交换机进行绑定
     * @return
     */
    @Bean
    public Binding bindOrder() {
        return BindingBuilder.bind(getOrderQueue()).to(getOrderExchange()).with(ORDER_ROUTING_KEY).noargs();
    }

    /**
     * 创建canal队列
     * @return
     */
    @Bean
    public Queue getCanalQueue() {
        return new Queue(CANAL_QUEUE);
    }

    /**
     * 创建canal交换机
     * @return
     */
    @Bean
    public Exchange getCanalExchange() {
        return ExchangeBuilder.directExchange(CANAL_EXCHANGE).durable(true).build();
    }

    /**
     * canal队列与canal交换机进行绑定
     * @return
     */
    @Bean
    public Binding bindCanal() {
        return BindingBuilder.bind(getCanalQueue()).to(getCanalExchange()).with(CANAL_ROUTING_KEY).noargs();
    }

    //声明交换机
    @Bean(EXCHANGE_TOPICS_INFORM)
    public Exchange EXCHANGE_TOPICS_INFORM(){
        //durable(true) 持久化，mq重启之后交换机还在
        return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_INFORM).durable(true).build();
    }

    //声明QUEUE_INFORM_EMAIL队列
    @Bean(QUEUE_INFORM_EMAIL)
    public Queue QUEUE_INFORM_EMAIL(){
        return new Queue(QUEUE_INFORM_EMAIL);
    }
    //声明QUEUE_INFORM_SMS队列
    @Bean(QUEUE_INFORM_SMS)
    public Queue QUEUE_INFORM_SMS(){
        return new Queue(QUEUE_INFORM_SMS);
    }

    //ROUTINGKEY_EMAIL队列绑定交换机，指定routingKey
    @Bean
    public Binding BINDING_QUEUE_INFORM_EMAIL(@Qualifier(QUEUE_INFORM_EMAIL) Queue queue,
                                              @Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_EMAIL).noargs();
    }
    //ROUTINGKEY_SMS队列绑定交换机，指定routingKey
    @Bean
    public Binding BINDING_ROUTINGKEY_SMS(@Qualifier(QUEUE_INFORM_SMS) Queue queue,
                                          @Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_SMS).noargs();
    }
    @Bean
    @Scope("prototype")//通知Spring把被注解的Bean变成多例 表示每次获得bean都会生成一个新的对象
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMandatory(true);
        template.setMessageConverter(new SerializerMessageConverter());
        return template;
    }

}
