package com.example.challenge.services;

import com.example.challenge.entities.RequestInformation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {

    private final RabbitTemplate rabbitTemplate;

    public ProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    public void sendMessage(RequestInformation requestInformation) {
        rabbitTemplate.convertAndSend("my-queue", requestInformation);
    }
}
