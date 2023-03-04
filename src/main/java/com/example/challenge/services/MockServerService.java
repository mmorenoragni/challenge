package com.example.challenge.services;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.json.JSONObject;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MockServerService {

    private final RetryTemplate retryTemplate;
    private final RestTemplate restTemplate;

    private JSONObject contingencyValue = null;

    public MockServerService(RestTemplate restTemplate, RetryTemplate retryTemplate) {
        this.restTemplate = restTemplate;
        this.retryTemplate = retryTemplate;
    }
    public JSONObject callMockServerService(MockResponse mockResponse) {

        JSONObject response = null;
        MockWebServer mockWebServer = new MockWebServer();

        try {
            // se mockea estos apicalls para demostrar que el retryTemplate llama 4 veces al servicio, el apicall esperados y 3 reintentos
            mockWebServer.enqueue(new MockResponse().setResponseCode(429));
            mockWebServer.enqueue(new MockResponse().setResponseCode(502));
            mockWebServer.enqueue(new MockResponse().setResponseCode(429));

            mockWebServer.enqueue(mockResponse);
            mockWebServer.start();
            HttpUrl url = mockWebServer.url("/mockserver");
            String StringResponse = retryTemplate.execute(retryContext -> restTemplate.getForObject(url.uri(), String.class));
            response = new JSONObject(StringResponse);
            mockWebServer.shutdown();
            contingencyValue = response;
        } catch (Exception ex) {
            if (contingencyValue == null) {
                throw new RuntimeException("No Contingency value assigned for fallback process");
            }

            response = contingencyValue;
        }

        return response;
    }
}
