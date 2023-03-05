package com.example.challenge.controllers;

import com.example.challenge.commons.SearchResponseWrapper;
import com.example.challenge.entities.OperationResult;
import com.example.challenge.entities.RequestInformation;
import com.example.challenge.exceptions.RateLimitException;
import com.example.challenge.services.OperationsService;
import com.example.challenge.services.ProducerService;
import com.example.challenge.services.RateLimiterService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ChallengeController {

    private final RateLimiterService rateLimiterService;
    private final ProducerService producerService;
    private final OperationsService operationsService;

    public ChallengeController(RateLimiterService rateLimiterService,
                               ProducerService producerService,
                               OperationsService operationsService) {

        this.rateLimiterService = rateLimiterService;
        this.producerService = producerService;
        this.operationsService = operationsService;
    }

    @GetMapping(value = "/challenge/addition", produces="application/json")
    public OperationResult additionController(@RequestParam float firstNum,
                                             @RequestParam float secondNum,
                                              HttpServletRequest request) {

        if (!rateLimiterService.resolveBucket("challengeBucket").tryConsume(1)) {
            throw new RateLimitException("Api Rate Limit has been exceeded");
        }

        OperationResult operationResult = operationsService.calculatePercentage(firstNum, secondNum);
        String urlConsumed = String.format("/challenge/find_operations%s", request.getQueryString());
        String responseInformation = operationResult.toJson();
        producerService.sendMessage(new RequestInformation(urlConsumed, responseInformation));
        return operationResult;
    }

    @GetMapping(value = "/challenge/find_operations")
    public ResponseEntity< SearchResponseWrapper<RequestInformation>> findAllOperations(@RequestParam(required = false, defaultValue = "0") int pageNumber, HttpServletRequest request) {

        if (!rateLimiterService.resolveBucket("challengeBucket").tryConsume(1)) {
            throw new RateLimitException("Api Rate Limit has been exceeded");
        }

        SearchResponseWrapper<RequestInformation> searchResponseWrapper = operationsService.findAllOperationsWithPaggination(pageNumber);
        String urlConsumed = String.format("/challenge/find_operations%s", request.getQueryString());
        String responseInformation = searchResponseWrapper.toJson();
        producerService.sendMessage(new RequestInformation(urlConsumed, responseInformation));
        return ResponseEntity.ok(searchResponseWrapper);
    }
}
