package com.aloima.urlshortener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.SecureRandom;

import org.junit.jupiter.api.Test;

import com.aloima.urlshortener.component.RandomIdGenerator;

class RandomIdGeneratorTest {

    @Test
    void generateValidType() throws Exception {
        RandomIdGenerator random = new RandomIdGenerator(new SecureRandom());
        String id = random.generateRandomId();

        assertTrue(id.matches("[A-Za-z0-9]+"));
        assertEquals(7, id.length());
    }
}
