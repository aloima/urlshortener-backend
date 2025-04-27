package com.aloima.urlshortener.repository;

import org.springframework.data.repository.CrudRepository;

import com.aloima.urlshortener.model.URLModel;

public interface URLRepository extends CrudRepository<URLModel, String> {
    
}
