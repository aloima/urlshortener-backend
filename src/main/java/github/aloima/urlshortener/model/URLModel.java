package github.aloima.urlshortener.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("URL")
public class URLModel implements Serializable {
    @Id
    private String id;

    private String value;
    private Date createdAt;
    public long clicks;

    public URLModel() {}

    public URLModel(String value, Date createdAt) {
        this.value = value;
        this.createdAt = createdAt;
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
}
