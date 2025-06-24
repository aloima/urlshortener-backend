package com.aloima.urlshortener.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
        return this.urlRepository.findById(Long.toString(random.stringToId(id)));
    }

    public List<URLModel> listURLs(long start, long end) {
        Iterable<URLModel> iterable = this.urlRepository.findAll();
        List<URLModel> list = StreamSupport.stream(iterable.spliterator(), true).collect(Collectors.toList());
        list.removeIf((url) -> !url.isListable());

        if (start == -1) {
            return list;
        } else {
            return list.subList((int) start, (int) end);
        }
    }

    public long countAllURL() {
        return this.urlRepository.count();
    }

    public long countListableURL() {
        return this.listURLs(-1, 0).size();
    }

    public long saveURL(URLModel data) {
        long urlId = this.random.generateRandomId();
        long deletionId = this.random.generateRandomId();

        while (this.urlRepository.existsById(Long.toString(urlId))) urlId = this.random.generateRandomId();
        while (this.deletionRepository.existsById(Long.toString(deletionId))) deletionId = this.random.generateRandomId();

        URLDeletionModel deletion = new URLDeletionModel(deletionId, urlId);
        deletionRepository.save(deletion);

        data.setId(urlId);
        data.setDeletionId(deletionId);
        urlRepository.save(data);

        return deletionId;
    }

    public boolean overwriteURL(long id, URLModel data) {
        if (data.getId() != id) return false;

        urlRepository.save(data);
        return true;
    }

    public boolean deleteURL(String id) {
        Optional<URLDeletionModel> deletion = this.deletionRepository.findById(Long.toString(random.stringToId(id)));
        if (deletion.isEmpty()) return false;

        URLDeletionModel deletionModel = deletion.get();
        this.urlRepository.deleteById(Long.toString(deletionModel.getValue()));
        this.deletionRepository.delete(deletionModel);

        return true;
    }
}