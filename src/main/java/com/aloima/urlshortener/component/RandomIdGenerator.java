package com.aloima.urlshortener.component;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class RandomIdGenerator {
    public final SecureRandom random = new SecureRandom();

    public long generateRandomId() {
        return this.random.nextLong(1, (long) Math.pow(59, 7));
    }

    public String idToString(long id) {
        String charset = "12345689abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ"; // without 0, l, I
        StringBuilder result = new StringBuilder("");

        while (id > 0) {
            result.append(charset.charAt(((int) Long.remainderUnsigned(id, 59))));
            id /= 59;
        }

        return result.toString();
    }

    public long stringToId(String value) {
        String charset = "12345689abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ"; // without 0, l, I
        long id = 0;

        int length = value.length();

        for (int i = 0; i < length; ++i) {
            id += (charset.indexOf(value.charAt(i)) * Math.pow(59, i));
        }

        return id;
    }
}
