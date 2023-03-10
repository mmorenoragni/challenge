package com.example.challenge.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @Operation(summary = "Este controller devuelve la resputa PONG " +
            "para indicar que el servicio esta levantado, su uso es interno, " +
            "no debe publicarse para ser consumido desde afuera")
    @GetMapping(value = "/ping")
    public String ping() {

        return "pong";
    }
}
