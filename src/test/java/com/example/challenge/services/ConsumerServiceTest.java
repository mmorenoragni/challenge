package com.example.challenge.services;

import com.example.challenge.entities.Operation;
import com.example.challenge.repositories.OperationRepository;
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
    private OperationRepository operationRepository;

    @Test
    public void testCallToSaveMethod() {
        Operation operation = new Operation(1d, 1d);
        consumerService.handleMyQueue(operation);
        verify(operationRepository).save(operation);
    }
}