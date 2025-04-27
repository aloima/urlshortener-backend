package github.aloima.urlshortener.repository;

import org.springframework.data.repository.CrudRepository;

import github.aloima.urlshortener.model.URLModel;

public interface URLRepository extends CrudRepository<URLModel, String> {
    
}
