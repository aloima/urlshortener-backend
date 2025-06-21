package com.aloima.urlshortener.controller;

import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aloima.urlshortener.exception.InvalidFormatException;
import com.aloima.urlshortener.exception.ResourceNotFoundException;
import com.aloima.urlshortener.model.URLModel;
import com.aloima.urlshortener.service.URLService;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping(value = "/url")
public class URLController {
    private final URLService urlService;

    public URLController(URLService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<URLModel> getURL(@PathVariable("id") String id) {
        Optional<URLModel> url = urlService.getURL(id);
        if (url.isEmpty()) throw new ResourceNotFoundException("URL with id '" + id + "' cannot be found.");

        return ResponseEntity.status(HttpStatus.OK).body(url.get());
    }

    @PostMapping
    public ResponseEntity<URLModel> newURL(@RequestBody JsonNode input) {
        if (!input.has("value")) throw new InvalidFormatException("Data must be include 'value' member.", input);
        JsonNode value = input.path("value");

        if (!value.isTextual()) throw new InvalidFormatException("'value' in data must be a string.", input);
        String rawValue = value.asText();
        
        if (rawValue.isBlank()) throw new InvalidFormatException("'value' in data must be a filled string.", input);

        URLModel data = new URLModel(value.asText(), new Date(), listableValue);
        urlService.saveURL(data);

        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(data);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteURL(@PathVariable String id) {
        boolean response = urlService.deleteURL(id);
        if (!response) throw new ResourceNotFoundException("URL with id '" + id + "' cannot be found, so it cannot be deleted.");

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
