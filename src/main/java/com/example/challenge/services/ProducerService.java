package com.example.challenge.services;

import com.example.challenge.entities.Operation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {

    private final RabbitTemplate rabbitTemplate;

    public ProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    public void sendMessage(Operation operation) {
        rabbitTemplate.convertAndSend("my-queue", operation);
    }
}
