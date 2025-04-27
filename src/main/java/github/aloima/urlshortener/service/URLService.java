package github.aloima.urlshortener.service;

import java.security.SecureRandom;
import java.util.Optional;

import org.springframework.stereotype.Service;

import github.aloima.urlshortener.model.URLDeletionModel;
import github.aloima.urlshortener.model.URLModel;
import github.aloima.urlshortener.repository.URLDeletionRepository;
import github.aloima.urlshortener.repository.URLRepository;

@Service
public class URLService {
    private final URLRepository url_repository;
    private final URLDeletionRepository deletion_repository;
    private final SecureRandom random;

    public URLService(URLRepository url_repository, URLDeletionRepository deletion_repository) {
        this.url_repository = url_repository;
        this.deletion_repository = deletion_repository;
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
        return this.url_repository.findById(id);
    }

    public void saveURL(URLModel data) {
        String url_id = generateRandomId();
        String deletion_id = generateRandomId();

        while (this.url_repository.existsById(url_id)) url_id = generateRandomId();
        while (this.deletion_repository.existsById(deletion_id)) deletion_id = generateRandomId();

        URLDeletionModel deletion = new URLDeletionModel(deletion_id, url_id);
        deletion_repository.save(deletion);

        data.setId(url_id);
        url_repository.save(data);
    }

    public boolean deleteURL(String id) {
        Optional<URLDeletionModel> deletion = deletion_repository.findById(id);
        if (deletion.isEmpty()) return false;

        URLDeletionModel deletionModel = deletion.get();
        this.url_repository.deleteById(deletionModel.getValue());
        this.deletion_repository.delete(deletionModel);

        return true;
    }
}