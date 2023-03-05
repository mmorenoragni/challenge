package com.example.challenge.entities;

public class OperationResult {

    private double result;

    public OperationResult(double result) {
        this.result = result;
    }

    public double getResult() {
        return result;
    }

    public String toJson() {
        return String.format("{\"result\" : %s}", result);
    }
}
