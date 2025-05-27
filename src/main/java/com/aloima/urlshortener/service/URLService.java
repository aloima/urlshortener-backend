package com.aloima.urlshortener.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.aloima.urlshortener.component.RandomIdGenerator;
import com.aloima.urlshortener.model.URLDeletionModel;
import com.aloima.urlshortener.model.URLModel;
import com.aloima.urlshortener.repository.URLDeletionRepository;
import com.aloima.urlshortener.repository.URLRepository;

@Service
public class URLService {
    private final URLRepository urlRepository;
    private final URLDeletionRepository deletionRepository;
    private final RandomIdGenerator random;

    public URLService(URLRepository urlRepository, URLDeletionRepository deletionRepository, RandomIdGenerator random) {
        this.urlRepository = urlRepository;
        this.deletionRepository = deletionRepository;
        this.random = random;
    }

    public Optional<URLModel> getURL(String id) {
        return this.urlRepository.findById(id);
    }

    public void saveURL(URLModel data) {
        long urlId = this.random.generateRandomId();
        long deletionId = this.random.generateRandomId();

        while (this.urlRepository.existsById(Long.toString(urlId))) urlId = this.random.generateRandomId();
        while (this.deletionRepository.existsById(Long.toString(deletionId))) deletionId = this.random.generateRandomId();

        URLDeletionModel deletion = new URLDeletionModel(deletionId, urlId);
        deletionRepository.save(deletion);

        data.setId(urlId);
        urlRepository.save(data);
    }

    public boolean deleteURL(String id) {
        Optional<URLDeletionModel> deletion = this.deletionRepository.findById(id);
        if (deletion.isEmpty()) return false;

        URLDeletionModel deletionModel = deletion.get();
        this.urlRepository.deleteById(Long.toString(deletionModel.getValue()));
        this.deletionRepository.delete(deletionModel);

        return true;
    }
}