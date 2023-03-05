package com.example.challenge.services;

import com.example.challenge.entities.Operation;
import com.example.challenge.entities.RequestInformation;
import com.example.challenge.repositories.RequestInformationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConsumerServiceTest {

    @InjectMocks
    private ConsumerService consumerService;

    @Mock
    private RequestInformationRepository operationRepository;

    @Test
    public void testCallToSaveMethod() {
        RequestInformation requestInformation = new RequestInformation("/mock_url", "\"mock\" : 123");
        consumerService.handleMyQueue(requestInformation);
        verify(operationRepository).save(requestInformation);
    }
}