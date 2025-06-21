package com.aloima.urlshortener.exception;

import com.fasterxml.jackson.databind.JsonNode;

public class HTTPErrorResponse {
    public final String error;
    public final String uri;
    public final JsonNode data;

    public HTTPErrorResponse(String error, String uri, JsonNode data) {
        this.error = error;
        this.uri = uri;
        this.data = data;
    }
}
