package com.aloima.urlshortener.service;

import java.security.SecureRandom;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.aloima.urlshortener.model.URLDeletionModel;
import com.aloima.urlshortener.model.URLModel;
import com.aloima.urlshortener.repository.URLDeletionRepository;
import com.aloima.urlshortener.repository.URLRepository;

@Service
public class URLService {
    private final URLRepository urlRepository;
    private final URLDeletionRepository deletionRepository;
    private final SecureRandom random;

    public URLService(URLRepository urlRepository, URLDeletionRepository deletionRepository) {
        this.urlRepository = urlRepository;
        this.deletionRepository = deletionRepository;
        this.random = new SecureRandom();
    }

    private String generateRandomId() {
        StringBuilder id = new StringBuilder("");
        String charset = "012345689abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        for (int i = 0; i < 7; ++i) {
            int index = random.nextInt(charset.length());
            id.append(charset.charAt(index));
        }

        return id.toString();
    }

    public Optional<URLModel> getURL(String id) {
        return this.urlRepository.findById(id);
    }

    public void saveURL(URLModel data) {
        String url_id = generateRandomId();
        String deletion_id = generateRandomId();

        while (this.urlRepository.existsById(url_id)) url_id = generateRandomId();
        while (this.deletionRepository.existsById(deletion_id)) deletion_id = generateRandomId();

        URLDeletionModel deletion = new URLDeletionModel(deletion_id, url_id);
        deletionRepository.save(deletion);

        data.setId(url_id);
        urlRepository.save(data);
    }

    public boolean deleteURL(String id) {
        Optional<URLDeletionModel> deletion = deletionRepository.findById(id);
        if (deletion.isEmpty()) return false;

        URLDeletionModel deletionModel = deletion.get();
        this.urlRepository.deleteById(deletionModel.getValue());
        this.deletionRepository.delete(deletionModel);

        return true;
    }
}