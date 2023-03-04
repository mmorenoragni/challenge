package com.example.challenge.services;

import com.example.challenge.entities.Operation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProducerServiceTest {

    @InjectMocks
    private ProducerService producerService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testCallToRabbitTemplate() {

        Operation operation = new Operation(1, 2);
        producerService.sendMessage(operation);

        verify(rabbitTemplate).convertAndSend("my-queue", operation);
    }
}