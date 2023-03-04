package com.example.challenge.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProducerConfig {

    @Bean
    public Queue myQueue() {  // creates my-queue automatically on the RabbitMQ server if not available
        return new Queue("my-queueproducer");
    }
}
