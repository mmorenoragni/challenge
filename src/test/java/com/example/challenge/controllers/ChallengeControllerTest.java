package com.example.challenge.controllers;

import com.example.challenge.commons.SearchResponseWrapper;
import com.example.challenge.entities.OperationResult;
import com.example.challenge.entities.RequestInformation;
import com.example.challenge.services.OperationsService;
import com.example.challenge.services.ProducerService;
import com.example.challenge.services.RateLimiterService;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ChallengeController.class)
@ExtendWith(MockitoExtension.class)
class ChallengeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RateLimiterService rateLimiterService;

    @MockBean
    private ProducerService producerService;

    @MockBean
    private OperationsService operationsService;

    @Mock
    Bucket mockBucket;

    @Test
    public void testPerformAnOperationAndGetAResult() throws Exception {

        when(rateLimiterService.resolveBucket("challengeBucket")).thenReturn(mockBucket);
        when(mockBucket.tryConsume(1)).thenReturn(true);
        when(operationsService.calculatePercentage(5, 1)).thenReturn(new OperationResult(6.36));

        ResultActions response = mockMvc.perform(get("/challenge/addition")
                                        .queryParam("firstNum", "5")
                                        .queryParam("secondNum", "1"));

        verify(producerService).sendMessage(any(RequestInformation.class));
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(6.36));

    }

    @Test
    public void testPerformAnOperationAndGetAnExceptionBecauseRateWasExceed() throws Exception {

        when(rateLimiterService.resolveBucket("challengeBucket")).thenReturn(mockBucket);
        when(mockBucket.tryConsume(1)).thenReturn(false);

        ResultActions response = mockMvc.perform(get("/challenge/addition")
                .queryParam("firstNum", "5")
                .queryParam("secondNum", "1"));

        response.andExpect(status().is(429))
                .andExpect(jsonPath("$.error").value("rate_limit"))
                .andExpect(jsonPath("$.message").value("Api Rate Limit has been exceeded"))
                .andExpect(jsonPath("$.status").value(429));

    }

    @Test
    public void testPerformAnOperationAndGetAnExceptionWhenParametersAreInvalids() throws Exception {

        ResultActions response = mockMvc.perform(get("/challenge/addition")
                .queryParam("firstNum", "0")
                .queryParam("secondNum", "1"));

        response.andExpect(status().is(400))
                .andExpect(jsonPath("$.error").value("bad_request"))
                .andExpect(jsonPath("$.message").value("parameters should be greater than zero"))
                .andExpect(jsonPath("$.status").value(400));

        ResultActions response2 = mockMvc.perform(get("/challenge/addition")
                .queryParam("firstNum", "1")
                .queryParam("secondNum", "0"));

        response2.andExpect(status().is(400))
                .andExpect(jsonPath("$.error").value("bad_request"))
                .andExpect(jsonPath("$.message").value("parameters should be greater than zero"))
                .andExpect(jsonPath("$.status").value(400));

    }

    @Test
    public void testPerformASearchAndGetAResultBecausePageNumberIsInvalid() throws Exception {

        when(rateLimiterService.resolveBucket("challengeBucket")).thenReturn(mockBucket);
        when(mockBucket.tryConsume(1)).thenReturn(true);

        ResultActions response = mockMvc.perform(get("/challenge/find_operations")
                .queryParam("pageNumber", "-5"));

        response.andExpect(status().is(400))
                .andExpect(jsonPath("$.error").value("bad_request"))
                .andExpect(jsonPath("$.message").value("pageNumber paramater should be equal or greater than zero"))
                .andExpect(jsonPath("$.status").value(400));

    }

    @Test
    public void testPerformASearchAndGetResult() throws Exception {

        RequestInformation operation = new RequestInformation("/mock","{\"mock\" : \"mock_value\"}");
        List<RequestInformation> operationsList = new ArrayList<>();
        operationsList.add(operation);
        SearchResponseWrapper<RequestInformation> searchResult = new SearchResponseWrapper<>(10,5, 10, operationsList);

        when(rateLimiterService.resolveBucket("challengeBucket")).thenReturn(mockBucket);
        when(mockBucket.tryConsume(1)).thenReturn(true);
        when(operationsService.findAllOperationsWithPaggination(5)).thenReturn(searchResult);

        ResultActions response = mockMvc.perform(get("/challenge/find_operations")
                .queryParam("pageNumber", "5"));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSize").value(10))
                .andExpect(jsonPath("$.currentPage").value(5))
                .andExpect(jsonPath("$.totalPages").value(10));

    }
}