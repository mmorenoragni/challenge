package com.example.challenge.services;

import com.example.challenge.entities.Operation;
import com.example.challenge.repositories.OperationRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

    private final OperationRepository operationRepository;

    public ConsumerService(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    @RabbitListener(queues = "my-queue")
    public void handleMyQueue(Operation operation) {
        operationRepository.save(operation);
    }
}
