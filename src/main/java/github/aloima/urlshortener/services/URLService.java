package github.aloima.urlshortener.services;

import java.security.SecureRandom;
import java.util.Optional;

import org.springframework.stereotype.Service;

import github.aloima.urlshortener.model.URLDeletionModel;
import github.aloima.urlshortener.model.URLModel;
import github.aloima.urlshortener.repositories.URLDeletionRepository;
import github.aloima.urlshortener.repositories.URLRepository;

@Service
public class URLService {
    public final URLRepository url_repository;
    public final URLDeletionRepository deletion_repository;
    private final SecureRandom random = new SecureRandom();

    public URLService(URLRepository url_repository, URLDeletionRepository deletion_repository) {
        this.url_repository = url_repository;
        this.deletion_repository = deletion_repository;
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

    public void saveUrl(URLModel data) {
        String url_id = generateRandomId();
        String deletion_id = generateRandomId();

        while (this.url_repository.existsById(url_id)) url_id = generateRandomId();
        while (this.deletion_repository.existsById(deletion_id)) deletion_id = generateRandomId();

        URLDeletionModel deletion = new URLDeletionModel(deletion_id, url_id);
        deletion_repository.save(deletion);

        data.setId(url_id);
        url_repository.save(data);
    }

    public boolean deleteUrl(String id) {
        Optional<URLDeletionModel> deletion = deletion_repository.findById(id);
        if (deletion.isEmpty()) return false;

        URLDeletionModel deletionModel = deletion.get();
        this.url_repository.deleteById(deletionModel.getValue());
        this.deletion_repository.delete(deletionModel);

        return true;
    }
}