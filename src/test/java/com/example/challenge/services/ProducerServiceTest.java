package com.example.challenge.services;

import com.example.challenge.entities.Operation;
import com.example.challenge.entities.RequestInformation;
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

        RequestInformation requestInformation = new RequestInformation("mock", "mock");
        producerService.sendMessage(requestInformation);

        verify(rabbitTemplate).convertAndSend("my-queue", requestInformation);
    }
}