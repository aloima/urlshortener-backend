package com.aloima.urlshortener.exception;

import com.fasterxml.jackson.databind.JsonNode;

public class InvalidFormatException extends RuntimeException {
    public JsonNode data;

    public InvalidFormatException(String message, JsonNode data) {
        super(message);

        this.data = data;
    }
}
