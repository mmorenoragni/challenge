package com.example.challenge.services;

import com.example.challenge.entities.Operation;
import com.example.challenge.entities.RequestInformation;
import com.example.challenge.repositories.RequestInformationRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

    private final RequestInformationRepository requestInformationRepository;

    public ConsumerService(RequestInformationRepository requestInformationRepository) {
        this.requestInformationRepository = requestInformationRepository;
    }

    @RabbitListener(queues = "my-queue")
    public void handleMyQueue(RequestInformation requestInformation) {
        requestInformationRepository.save(requestInformation);
    }
}
