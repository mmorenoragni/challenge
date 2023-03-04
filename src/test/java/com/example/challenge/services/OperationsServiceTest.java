package com.example.challenge.services;

import com.example.challenge.commons.SearchResponseWrapper;
import com.example.challenge.entities.Operation;
import com.example.challenge.entities.OperationResult;
import com.example.challenge.repositories.OperationRepository;
import com.squareup.okhttp.mockwebserver.MockResponse;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RAtomicDouble;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperationsServiceTest {

    @Mock
    private  MockServerService mockServerService;

    @Mock
    private  OperationRepository operationRepository;

    @Mock
    private RAtomicDouble mockCacheResult;

    @Mock
    private Page<Operation> mockPaggeableResult;

    @Mock
    private  RedissonClient redissonClient;

    @Mock
    private Pageable mockPaggeable;

    @InjectMocks
    private OperationsService operationsService;

    @Test
    public void calculatePercentageWhenFirstNumbersIs5AndSecondNumberIs10AndValueIsNotInCache() {

        JSONObject mockResponse = new JSONObject("{\"result\" : 1.15}");

        when(redissonClient.getAtomicDouble("operationResult")).thenReturn(mockCacheResult);
        when(mockCacheResult.getExpireTime()).thenReturn(-2L);
        when(mockServerService.callMockServerService(any(MockResponse.class))).thenReturn(mockResponse);

        OperationResult result = operationsService.calculatePercentage(5, 10);

        assertEquals(17.25, result.getResult());
        verify(redissonClient).getAtomicDouble("operationResult");
        verify(mockCacheResult).getExpireTime();
        verify(mockServerService).callMockServerService(any(MockResponse.class));
        verify(mockCacheResult).set(1.15d);
        verify(mockCacheResult).expire(Duration.ofMinutes(30));
    }

    @Test
    public void calculatePercentageWhenFirstNumbersIs5AndSecondNumberIs10AndValueIsPresentInCache() {

        JSONObject mockResponse = new JSONObject("{\"result\" : 1.15}");

        when(redissonClient.getAtomicDouble("operationResult")).thenReturn(mockCacheResult);
        when(mockCacheResult.getExpireTime()).thenReturn(10L);
        when(mockCacheResult.get()).thenReturn(1.15d);

        OperationResult result = operationsService.calculatePercentage(5, 10);
        assertEquals(17.25, result.getResult());
    }

    @Test
    public void testFindAllOperationsWithPaggination() {

        List<Operation> operationList = new ArrayList<>();
        Operation operation1 = new Operation(10, 5);
        Operation operation2 = new Operation(10, 10);
        Operation operation3 = new Operation(5, 5);
        operationList.add(operation1);
        operationList.add(operation2);
        operationList.add(operation3);

        when(mockPaggeableResult.get()).thenReturn(operationList.stream());
        when(operationRepository.findAllWithPaggination(any(Pageable.class))).thenReturn(mockPaggeableResult);
        when(mockPaggeableResult.getTotalPages()).thenReturn(5);
        when(mockPaggeableResult.getTotalElements()).thenReturn(10L);
        when(mockPaggeableResult.getPageable()).thenReturn(mockPaggeable);
        when(mockPaggeable.getPageNumber()).thenReturn(3);

        SearchResponseWrapper<Operation> searchResult = operationsService.findAllOperationsWithPaggination(5);

        assertEquals(3, searchResult.getContent().size());
        assertEquals(10, searchResult.getContent().get(0).getFirstNum());
        assertEquals(5, searchResult.getContent().get(0).getSecondNum());
        assertNull(searchResult.getContent().get(0).getId());
        assertEquals(3L, searchResult.getCurrentPage());
        assertEquals(10, searchResult.getTotalSize());
        assertEquals(5, searchResult.getTotalPages());
    }
}