package github.aloima.urlshortener.repositories;

import org.springframework.data.repository.CrudRepository;

import github.aloima.urlshortener.model.URLModel;

public interface URLRepository extends CrudRepository<URLModel, String> {

}
