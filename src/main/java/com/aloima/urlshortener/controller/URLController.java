package com.aloima.urlshortener.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.aloima.urlshortener.exception.InvalidFormatException;
import com.aloima.urlshortener.exception.ResourceNotFoundException;
import com.aloima.urlshortener.model.URLModel;
import com.aloima.urlshortener.service.URLService;

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

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listURLs(@RequestParam(defaultValue = "-1") long start, @RequestParam(defaultValue = "0") long end) {
        long totalCount = urlService.countAllURL();
        long listableCount = urlService.countListableURL();

        if (end > listableCount) end = listableCount;
        if (start < -1) start = 0;

        List<URLModel> data = urlService.listURLs(start, end);
        Map<String, Object> body = new HashMap<>();

        body.put("totalCount", totalCount);
        body.put("listableCount", listableCount);
        body.put("data", data);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @GetMapping("/go/{id}")
    public ResponseEntity<String> goURL(@PathVariable("id") String id) {
        Optional<URLModel> urlField = urlService.getURL(id);
        if (urlField.isEmpty()) throw new ResourceNotFoundException("URL with id '" + id + "' cannot be found.");

        URLModel url = urlField.get();
        url.increaseClicks();
        urlService.overwriteURL(url.getId(), url);

        return ResponseEntity.status(HttpStatus.OK).body(url.getValue());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> newURL(@RequestBody JsonNode input) {
        if (!input.has("value")) throw new InvalidFormatException("Data must be include 'value' member.", input);
        JsonNode value = input.path("value");

        if (!value.isTextual()) throw new InvalidFormatException("'value' in data must be a string.", input);
        String rawValue = value.asText();
        
        if (rawValue.isBlank()) throw new InvalidFormatException("'value' in data must be a filled string.", input);

        JsonNode listable = input.path("listable");
        boolean listableValue = false;

        if (!listable.isMissingNode()) {
            if (!listable.isBoolean()) throw new InvalidFormatException("'listable' in data must be a boolean.", input);
            listableValue = listable.asBoolean();
        }

        URLModel data = new URLModel(value.asText(), new Date(), listableValue);
        long deletionId = urlService.saveURL(data);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> body = mapper.convertValue(data, new TypeReference<Map<String, Object>>() {});
        body.put("deletionId", deletionId);

        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteURL(@PathVariable String id) {
        boolean response = urlService.deleteURL(id);
        if (!response) throw new ResourceNotFoundException("URL with id '" + id + "' cannot be found, so it cannot be deleted.");

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
