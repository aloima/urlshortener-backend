package github.aloima.urlshortener.repositories;

import org.springframework.data.repository.CrudRepository;

import github.aloima.urlshortener.model.URLDeletionModel;

public interface URLDeletionRepository extends CrudRepository<URLDeletionModel, String> {
    
}
