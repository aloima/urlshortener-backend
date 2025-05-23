package com.aloima.urlshortener.exception;

public class HTTPErrorResponse {
    public final String error;
    public final String uri;

    public HTTPErrorResponse(String error, String uri) {
        this.error = error;
        this.uri = uri;
    }
}
