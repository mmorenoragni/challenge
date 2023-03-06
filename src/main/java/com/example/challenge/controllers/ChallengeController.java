package com.example.challenge.controllers;

import com.example.challenge.commons.SearchResponseWrapper;
import com.example.challenge.entities.OperationResult;
import com.example.challenge.entities.RequestInformation;
import com.example.challenge.exceptions.BadRequestException;
import com.example.challenge.exceptions.RateLimitException;
import com.example.challenge.services.OperationsService;
import com.example.challenge.services.ProducerService;
import com.example.challenge.services.RateLimiterService;

import io.swagger.v3.oas.annotations.Operation;
import org.apache.avro.message.BadHeaderException;
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

    @Operation(summary ="Este endpoint recibe dos numeros como parametro y devuelve como resultado la suma de ambos " +
            "numeros a la que se le aplica un porcentaje que resulta como valr de la suma de los dos numeros dividios por cien")
    @GetMapping(value = "/challenge/addition")
    public OperationResult additionController(@RequestParam float firstNum,
                                             @RequestParam float secondNum,
                                              HttpServletRequest request) {

        if (firstNum < 1 || secondNum < 1) {
            throw new BadRequestException("parameters should be greater than zero");
        }
        if (!rateLimiterService.resolveBucket("challengeBucket").tryConsume(1)) {
            throw new RateLimitException("Api Rate Limit has been exceeded");
        }

        OperationResult operationResult = operationsService.calculatePercentage(firstNum, secondNum);
        String urlConsumed = String.format("/challenge/find_operations%s", request.getQueryString());
        String responseInformation = operationResult.toJson();
        producerService.sendMessage(new RequestInformation(urlConsumed, responseInformation));
        return operationResult;
    }

    @Operation(summary ="Este endpoint devuelve como respuesta las consultas que se realizaron a la api junto a las respuestas que la api devolvió, la respuesta es en formato Json y y contiene como datos: totalSize [este valor representa la cantidad total de registros que hay en la DB], currentPage [indica la pagina actual que esta mostrando la búsqueda], totalPages [Indica la cantidad total de paginas que tiene que devuelve la consulta] y  content [es un listado de los registros que trae la consulta en la DB]")
    @GetMapping(value = "/challenge/find_operations")
    public ResponseEntity< SearchResponseWrapper<RequestInformation>> findAllOperations(@RequestParam(required = false, defaultValue = "0") int pageNumber, HttpServletRequest request) {

        if (!rateLimiterService.resolveBucket("challengeBucket").tryConsume(1)) {
            throw new RateLimitException("Api Rate Limit has been exceeded");
        }

        if (pageNumber < 0 ) {
            throw new BadRequestException("pageNumber paramater should be equal or greater than zero");
        }

        SearchResponseWrapper<RequestInformation> searchResponseWrapper = operationsService.findAllOperationsWithPaggination(pageNumber);
        String urlConsumed = String.format("/challenge/find_operations%s", request.getQueryString());
        String responseInformation = searchResponseWrapper.toJson();
        producerService.sendMessage(new RequestInformation(urlConsumed, responseInformation));
        return ResponseEntity.ok(searchResponseWrapper);
    }
}
