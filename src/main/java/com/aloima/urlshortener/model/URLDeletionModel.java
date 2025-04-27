package com.aloima.urlshortener.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("URLDeletion")
public class URLDeletionModel implements Serializable {
    @Id
    private String id;
    private String value;

    public URLDeletionModel(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}