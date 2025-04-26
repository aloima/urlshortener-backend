package github.aloima.urlshortener.controller;

import java.util.Date;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
        Optional<URLModel> url = urlService.url_repository.findById(id);
        return url.isEmpty() ? "" : url.get().getValue();
    }

    @GetMapping("/get-info/{id}")
    public URLModel getInfo(@PathVariable("id") String id) {
        Optional<URLModel> url = urlService.url_repository.findById(id);
        return url.orElseGet(() -> empty_url);
    }

    @PostMapping("/add")
    public void add(@RequestBody String value) {
        URLModel data = new URLModel(value, new Date());
        urlService.saveUrl(data);
    }

    @RequestMapping(path = "/delete/{id}", produces = "application/json")
    public String delete(@PathVariable String id) {
        boolean response = urlService.deleteUrl(id);
        return String.format("{\"success\": %s}", response ? "true" : "false");
    }
}
