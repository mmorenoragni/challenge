package com.example.challenge.services;

import com.example.challenge.commons.SearchResponseWrapper;
import com.example.challenge.entities.OperationResult;
import com.example.challenge.entities.RequestInformation;
import com.example.challenge.repositories.RequestInformationRepository;
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
    private RequestInformationRepository requestInformationRepository;

    @Mock
    private RAtomicDouble mockCacheResult;

    @Mock
    private Page<RequestInformation> mockPaggeableResult;

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

        List<RequestInformation> requestInformationList = new ArrayList<>();
        RequestInformation requestInformation1 = new RequestInformation("/mock", "{\"mock\" : \"mock\"}");
        RequestInformation requestInformation2 = new RequestInformation("/search_mock", "{}");
        RequestInformation requestInformation3 = new RequestInformation("/mock_url", "{}");
        requestInformationList.add(requestInformation1);
        requestInformationList.add(requestInformation2);
        requestInformationList.add(requestInformation3);

        when(mockPaggeableResult.get()).thenReturn(requestInformationList.stream());
        when(requestInformationRepository.findAllWithPaggination(any(Pageable.class))).thenReturn(mockPaggeableResult);
        when(mockPaggeableResult.getTotalPages()).thenReturn(5);
        when(mockPaggeableResult.getTotalElements()).thenReturn(10L);
        when(mockPaggeableResult.getPageable()).thenReturn(mockPaggeable);
        when(mockPaggeable.getPageNumber()).thenReturn(3);

        SearchResponseWrapper<RequestInformation> searchResult = operationsService.findAllOperationsWithPaggination(5);

        assertEquals(3, searchResult.getContent().size());
        assertEquals("/mock", searchResult.getContent().get(0).getUrlInformation());
        assertEquals("{\"mock\" : \"mock\"}", searchResult.getContent().get(0).getResponseInformation());
        assertEquals(3L, searchResult.getCurrentPage());
        assertEquals(10, searchResult.getTotalSize());
        assertEquals(5, searchResult.getTotalPages());
    }
}