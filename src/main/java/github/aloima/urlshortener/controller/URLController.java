package github.aloima.urlshortener.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import github.aloima.urlshortener.model.URLModel;
import github.aloima.urlshortener.services.URLService;

@RestController
public class URLController {
    private final URLService urlService;
    private final URLModel empty_url = new URLModel();

    public URLController(URLService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/get/{id}")
    public String get(@PathVariable("id") String id) {
        Optional<URLModel> url = urlService.repository.findById(id);
        return url.isEmpty() ? "" : url.get().getValue();
    }

    @GetMapping("/get-info/{id}")
    public URLModel getInfo(@PathVariable("id") String id) {
        Optional<URLModel> url = urlService.repository.findById(id);
        return url.orElseGet(() -> empty_url);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        return ResponseEntity.ok("error-general");
    }
}
