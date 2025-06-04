package com.aloima.urlshortener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.aloima.urlshortener.component.RandomIdGenerator;

class RandomIdGeneratorTest {
    private RandomIdGenerator generator;

    @BeforeEach
    void createRandomIdGenerator() {
        generator = new RandomIdGenerator();
        assertNotNull(generator);
    }

    @Test
    void generateValidValue() throws Exception {
        long id = generator.generateRandomId();
        assertNotNull(id);

        assertTrue(1 <= id);
        assertTrue(id < Math.pow(59, 7));
    }

    @Test
    void checkValidConversions() throws Exception {
        long generated = generator.generateRandomId();
        String value = generator.idToString(generated);
        assertNotNull(value);

        assertTrue(value.matches("[A-Za-z0-9]+"));
        assertTrue(1 <= value.length());
        assertTrue(value.length() <= 7);

        long id = generator.stringToId(value);
        assertNotNull(id);
        assertEquals(id, generated);
    }
}
