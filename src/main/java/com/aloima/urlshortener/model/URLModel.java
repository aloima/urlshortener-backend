package com.aloima.urlshortener.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("URL")
public class URLModel implements Serializable {
    @Id
    private String id;

    private final String value;
    private final Date createdAt;
    private long clicks;

    public URLModel() {
        this.value = null;
        this.createdAt = null;
    }

    public URLModel(String value, Date createdAt) {
        this.value = value;
        this.createdAt = createdAt;
        this.clicks = 0;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public long getClicks() {
        return this.clicks;
    }

    public void incrementClicks() {
        this.clicks += 1;
    }
}
