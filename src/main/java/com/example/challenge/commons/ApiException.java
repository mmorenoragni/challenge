package com.example.challenge.commons;

import org.json.JSONObject;

public class ApiException extends Exception {
    private static final long serialVersionUID = 1L;
    private final String code;
    private final String description;
    private final Integer statusCode;

    public ApiException(String code, String description, Integer statusCode) {
        this.code = code;
        this.description = description;
        this.statusCode = statusCode;
    }

    public String getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    public Integer getStatusCode() {
        return this.statusCode;
    }

    public String toJson() {
        JSONObject json = new JSONObject();
        json.put("error", getCode());
        json.put("message", getDescription());
        json.put("status", getStatusCode());
        return json.toString();
    }
}
