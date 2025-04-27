package github.aloima.urlshortener.repository;

import org.springframework.data.repository.CrudRepository;

import github.aloima.urlshortener.model.URLDeletionModel;

public interface URLDeletionRepository extends CrudRepository<URLDeletionModel, String> {
    
}
