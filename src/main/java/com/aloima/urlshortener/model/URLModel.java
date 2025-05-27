package com.aloima.urlshortener.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("URL")
public class URLModel implements Serializable {
    @Id
    private long id;

    @SuppressWarnings("unused")
    private long deletionId;

    private String value;
    private Date createdAt;
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

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDeletionId(long deletionId) {
        this.deletionId = deletionId;
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
