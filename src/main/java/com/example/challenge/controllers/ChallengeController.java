package com.example.challenge.controllers;

import com.example.challenge.commons.SearchResponseWrapper;
import com.example.challenge.entities.Operation;
import com.example.challenge.entities.OperationResult;
import com.example.challenge.exceptions.RateLimitException;
import com.example.challenge.services.OperationsService;
import com.example.challenge.services.ProducerService;
import com.example.challenge.services.RateLimiterService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
                                             @RequestParam float secondNum) {

        if (!rateLimiterService.resolveBucket("challengeBucket").tryConsume(1)) {
            throw new RateLimitException("Api Rate Limit has been exceeded");
        }

        producerService.sendMessage(new Operation(firstNum, secondNum));
        return operationsService.calculatePercentage(firstNum, secondNum);
    }

    @GetMapping(value = "/challenge/find_operations")
    public SearchResponseWrapper<Operation> findAllOperations(@RequestParam(required = false, defaultValue = "0") int pageNumber) {

        if (!rateLimiterService.resolveBucket("challengeBucket").tryConsume(1)) {
            throw new RateLimitException("Api Rate Limit has been exceeded");
        }

        return operationsService.findAllOperationsWithPaggination(pageNumber);
    }
}
