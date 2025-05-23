package com.aloima.urlshortener.controller;

import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void newURL(@RequestBody String value) {
        URLModel data = new URLModel(value, new Date());
        urlService.saveURL(data);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/{id}", produces = "application/json")
    public void deleteURL(@PathVariable String id) {
        boolean response = urlService.deleteURL(id);
        if (!response) throw new ResourceNotFoundException("URL with id '" + id + "' cannot be found, so it cannot be deleted.");
    }
}
