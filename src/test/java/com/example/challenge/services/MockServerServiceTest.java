package com.example.challenge.services;

import com.squareup.okhttp.mockwebserver.MockResponse;
import org.json.JSONObject;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class MockServerServiceTest {

    //Acá se testea contra el objeto concreto, ya que el server es mock, caso contrario, habría que usar los templates mocks
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RetryTemplate retryTemplate;

    @Autowired
    private MockServerService mockServerService;

    @Test
    @Order(1)
    public void testShouldReturnExceptionWhenApiCallFailsAndNotFallbackValueWasSets() {

        MockResponse mockResponse = new MockResponse().setResponseCode(500);

        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> {
            mockServerService.callMockServerService(mockResponse);
        });

        assertEquals("No Contingency value assigned for fallback process", thrown.getMessage());
    }

    @Test
    @Order(2)
    public void testReturnExpectedJsonObjectWithValueWhenCallToExternalServiceWasSuccess() {

        MockResponse mockResponse = new MockResponse().setResponseCode(200).setBody("{\"result\": 1.20}");

        JSONObject response = mockServerService.callMockServerService(mockResponse);
        assertEquals(response.getDouble("result"), 1.20);

    }

    @Test
    @Order(3)
    public void testReturnContingencyValueWhenApiCallFails() {

        MockResponse mockResponse = new MockResponse().setResponseCode(500);

        JSONObject response = mockServerService.callMockServerService(mockResponse);
        assertEquals(response.getDouble("result"), 1.20);

    }

}