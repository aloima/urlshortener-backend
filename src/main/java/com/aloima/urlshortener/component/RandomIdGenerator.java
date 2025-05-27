package com.aloima.urlshortener.component;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class RandomIdGenerator {
    public final SecureRandom random;

    public RandomIdGenerator(SecureRandom random) {
        this.random = random;
    }

    public long generateRandomId() {
        return this.random.nextLong(1, (long) Math.pow(62, 7));
    }

    public String idToString(long id) {
        String charset = "012345689abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder result = new StringBuilder("");

        while (id > 0) {
            result.append(charset.charAt(((int) Long.remainderUnsigned(id, 62))));
            id /= 62;
        }

        return result.toString();
    }

    public long stringToId(String value) {
        String charset = "012345689abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        long id = 0;

        int length = value.length();

        for (int i = 0; i < length; ++i) {
            id += (charset.indexOf(value.charAt(i)) * Math.pow(62, i));
        }

        return id;
    }
}
