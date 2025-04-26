package github.aloima.urlshortener.services;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;

import github.aloima.urlshortener.repositories.URLRepository;

@Service
public class URLService {
    public final URLRepository repository;
    private final SecureRandom random = new SecureRandom();

    public URLService(URLRepository repository) {
        this.repository = repository;
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

    public String getUniqueId() {
        String id = generateRandomId();

        while (this.repository.existsById(id)) {
            id = generateRandomId();
        }

        return id;
    }
}