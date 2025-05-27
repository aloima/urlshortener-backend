package com.aloima.urlshortener.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("URLDeletion")
public class URLDeletionModel implements Serializable {
    @Id
    private long id;
    private long value;

    public URLDeletionModel(long id, long value) {
        this.id = id;
        this.value = value;
    }

    public long getValue() {
        return this.value;
    }
}