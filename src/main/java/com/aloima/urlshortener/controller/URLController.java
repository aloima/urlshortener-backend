package com.aloima.urlshortener.controller;

import java.util.Date;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aloima.urlshortener.model.URLModel;
import com.aloima.urlshortener.service.URLService;

@RestController
@RequestMapping(value = "/url")
public class URLController {
    private final URLService urlService;
    private final URLModel emptyURL = new URLModel();

    public URLController(URLService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/{id}")
    public URLModel getURL(@PathVariable("id") String id) {
        Optional<URLModel> url = urlService.getURL(id);
        return url.orElseGet(() -> emptyURL);
    }

    @PostMapping
    public void newURL(@RequestBody String value) {
        URLModel data = new URLModel(value, new Date());
        urlService.saveURL(data);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/{id}", produces = "application/json")
    public String deleteURL(@PathVariable String id) {
        boolean response = urlService.deleteURL(id);

        if (response) return "{\"success\": true}";
        else return "{\"success\": false}";
    }
}
