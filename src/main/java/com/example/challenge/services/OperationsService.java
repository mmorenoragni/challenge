package com.example.challenge.services;

import com.example.challenge.commons.SearchResponseWrapper;
import com.example.challenge.entities.OperationResult;
import com.example.challenge.entities.RequestInformation;
import com.example.challenge.repositories.RequestInformationRepository;
import com.squareup.okhttp.mockwebserver.MockResponse;
import org.json.JSONObject;
import org.redisson.api.RAtomicDouble;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OperationsService {

    private final MockServerService mockServerService;
    private final RequestInformationRepository requestInformationRepository;
    private final RedissonClient redissonClient;
    private final static String RESULT_KEY = "{\"result\": %s}";
    private final static int PAGE_SIZE = 5;

    public OperationsService(MockServerService mockServerService,
                             RequestInformationRepository requestInformationRepository,
                             RedissonClient redissonClient) {
        this.mockServerService = mockServerService;
        this.requestInformationRepository = requestInformationRepository;
        this.redissonClient = redissonClient;
    }

    public OperationResult calculatePercentage(float firstNumber, float secondNumber) {

        RAtomicDouble cachedResult = redissonClient.getAtomicDouble("operationResult");
        float numSumPercentageResult = (firstNumber + secondNumber) / 100;
        double result;

        if (cachedResult.getExpireTime() < 0) {

            String expectedResponse = String.format(RESULT_KEY, 1 + numSumPercentageResult);
            MockResponse mockResponse = new MockResponse().setResponseCode(200).setBody(expectedResponse);
            JSONObject mockServiceResponse = mockServerService.callMockServerService(mockResponse);
            double percentage = mockServiceResponse.getDouble("result");
            cachedResult.set(percentage);
            cachedResult.expire(Duration.ofMinutes(30));
            result = (firstNumber + secondNumber) * percentage;

        } else {
            result = (firstNumber + secondNumber) * cachedResult.get();
        }

        return new OperationResult(result);
    }

    public SearchResponseWrapper<RequestInformation> findAllOperationsWithPaggination(int pageNumer) {
        Pageable pageable = PageRequest.of(pageNumer, PAGE_SIZE);
        Page<RequestInformation> result =  requestInformationRepository.findAllWithPaggination(pageable);
        List<RequestInformation> content = result.get().collect(Collectors.toList());
        int pages = result.getTotalPages();
        long totalElements = result.getTotalElements();
        int totalPages = result.getPageable().getPageNumber();
        return new SearchResponseWrapper<>(totalElements,totalPages, pages, content);
    }
}
