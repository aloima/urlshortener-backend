package com.aloima.urlshortener.component;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class RandomIdGenerator {
    public final SecureRandom random;

    public RandomIdGenerator(SecureRandom random) {
        this.random = random;
    }

    public String generateRandomId() {
        StringBuilder id = new StringBuilder("");
        String charset = "012345689abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        for (int i = 0; i < 7; ++i) {
            int index = random.nextInt(charset.length());
            id.append(charset.charAt(index));
        }

        return id.toString();
    }
}
