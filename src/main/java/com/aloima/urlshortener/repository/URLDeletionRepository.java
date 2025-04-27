package com.aloima.urlshortener.repository;

import org.springframework.data.repository.CrudRepository;

import com.aloima.urlshortener.model.URLDeletionModel;

public interface URLDeletionRepository extends CrudRepository<URLDeletionModel, String> {
    
}
