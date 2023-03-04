package com.example.challenge.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class ConsumerConfig {

    @Bean
    public Queue myQueueConsumer() {  // creates my-queue automatically on the RabbitMQ server if not available
        return new Queue("my-queue");
    }
}
