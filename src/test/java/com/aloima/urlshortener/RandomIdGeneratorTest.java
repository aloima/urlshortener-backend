package com.aloima.urlshortener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.aloima.urlshortener.component.RandomIdGenerator;

class RandomIdGeneratorTest {
    @Test
    void generateValidValue() throws Exception {
        RandomIdGenerator random = new RandomIdGenerator();
        assertNotNull(random);

        long id = random.generateRandomId();
        assertNotNull(id);

        assertTrue(1 <= id);
        assertTrue(id < Math.pow(62, 7));
    }

    @Test
    void checkValidConversions() throws Exception {
        RandomIdGenerator random = new RandomIdGenerator();
        assertNotNull(random);

        long generated = random.generateRandomId();
        String value = random.idToString(generated);
        assertNotNull(value);

        assertTrue(value.matches("[A-Za-z0-9]+"));
        assertTrue(1 <= value.length());
        assertTrue(value.length() <= 7);

        long id = random.stringToId(value);
        assertNotNull(id);
        assertEquals(id, generated);
    }
}
