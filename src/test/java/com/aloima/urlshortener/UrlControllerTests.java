package com.aloima.urlshortener;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.aloima.urlshortener.component.RandomIdGenerator;
import com.aloima.urlshortener.model.URLDeletionModel;
import com.aloima.urlshortener.model.URLModel;
import com.aloima.urlshortener.repository.URLDeletionRepository;
import com.aloima.urlshortener.repository.URLRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
class UrlControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private URLRepository urlRepository;

    @MockitoBean
    private URLDeletionRepository urlDeletionRepository;

    @MockitoBean
    private RandomIdGenerator random;

    @Test
    void getInvalidURL() throws Exception {
        this.mockMvc.perform(get("/url/unknown"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().json("{\"error\": \"URL with id 'unknown' cannot be found.\", \"uri\": \"/url/unknown\"}"));
    }

    @Test
    void getValidURL() throws Exception {
        URLModel url = new URLModel("https://example.com/", new Date(), false);
        url.setId(1L);
        url.setDeletionId(187L);

        when(urlRepository.findById(Long.toString(random.stringToId("1")))).thenReturn(Optional.of(url));

        this.mockMvc.perform(get("/url/1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpectAll(
                jsonPath("$.id").value(1L),
                jsonPath("$.value").value("https://example.com/"),
                jsonPath("$.clicks").value(0),
                jsonPath("$.createdAt").isString()
            );
    }

    @Test
    void deleteInvalidURL() throws Exception {
        this.mockMvc.perform(delete("/url/unknown"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().json("{\"error\": \"URL with id 'unknown' cannot be found, so it cannot be deleted.\", \"uri\": \"/url/unknown\"}"));
    }

    @Test
    void deleteValidURL() throws Exception {
        URLModel url = new URLModel("https://example.com/", new Date(), false);
        url.setId(1L);
        url.setDeletionId(187L);

        URLDeletionModel urlDeletion = new URLDeletionModel(1L, 187L);

        when(urlRepository.findById(Long.toString(random.stringToId("1")))).thenReturn(Optional.of(url));
        when(urlDeletionRepository.findById(Long.toString(random.stringToId("13")))).thenReturn(Optional.of(urlDeletion));

        this.mockMvc.perform(delete("/url/" + url.getId()))
            .andDo(print())
            .andExpect(status().isNoContent());
    }

    @Test
    void createValidURL() throws Exception {
        String value = "https://example.com/";
        String content = String.format("{\"value\": \"%s\"}", value);

        this.mockMvc.perform(post("/url").content(content).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpectAll(
                jsonPath("$.id").value(0L),
                jsonPath("$.value").value(value),
                jsonPath("$.clicks").value(0),
                jsonPath("$.createdAt").isString()
            );
    }

    @Test
    void createURLWithInvalidValue() throws Exception {
        String content = "{\"value\": true}";

        this.mockMvc.perform(post("/url").content(content).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpectAll(
                jsonPath("$.error").value("'value' in data must be a string."),
                jsonPath("$.uri").value("/url")
            );
    }

    @Test
    void createURLWithMissingValue() throws Exception {
        String content = "{}";

        this.mockMvc.perform(post("/url").content(content).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpectAll(
                jsonPath("$.error").value("Data must be include 'value' member."),
                jsonPath("$.uri").value("/url")
            );
    }

    @Test
    void createURLWithEmptyValue() throws Exception {
        String content = "{\"value\": \"\"}";

        this.mockMvc.perform(post("/url").content(content).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpectAll(
                jsonPath("$.error").value("'value' in data must be a filled string."),
                jsonPath("$.uri").value("/url")
            );
    }

    @Test
    void listURLWithQueries() throws Exception {
        String firstValue = "https://example-first.com/";
        String secondValue = "https://example-second.com/";
        String thirdValue = "https://example-third.com/";

        URLModel firstURL = new URLModel(firstValue, new Date(), false);
        firstURL.setId(1L);
        firstURL.setDeletionId(2L);

        URLModel secondURL = new URLModel(secondValue, new Date(), true);
        secondURL.setId(3L);
        secondURL.setDeletionId(4L);

        URLModel thirdURL = new URLModel(thirdValue, new Date(), true);
        thirdURL.setId(5L);
        thirdURL.setDeletionId(6L);

        when(urlRepository.count()).thenReturn(3L);
        when(urlRepository.findAll()).thenReturn(Arrays.asList(firstURL, secondURL, thirdURL));

        this.mockMvc.perform(get("/url/list?start=0&end=2"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpectAll(
                jsonPath("$.data").isArray(),
                jsonPath("$.data").value(hasSize(2)),
                jsonPath("$.data[?(@.id == %d && @.value == '%s')]".formatted(secondURL.getId(), secondURL.getValue())).exists(),
                jsonPath("$.data[?(@.id == %d && @.value == '%s')]".formatted(thirdURL.getId(), thirdURL.getValue())).exists(),
                jsonPath("$.totalCount").value(3),
                jsonPath("$.listableCount").value(2)
            );

        this.mockMvc.perform(get("/url/list?start=1&end=2"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpectAll(
                jsonPath("$.data").isArray(),
                jsonPath("$.data").value(hasSize(1)),
                jsonPath("$.data[?(@.id == %d && @.value == '%s')]".formatted(thirdURL.getId(), thirdURL.getValue())).exists(),
                jsonPath("$.totalCount").value(3),
                jsonPath("$.listableCount").value(2)
            );

        this.mockMvc.perform(get("/url/list"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpectAll(
                jsonPath("$.data").value(hasSize(2)),
                jsonPath("$.data[?(@.id == %d && @.value == '%s')]".formatted(secondURL.getId(), secondURL.getValue())).exists(),
                jsonPath("$.data[?(@.id == %d && @.value == '%s')]".formatted(thirdURL.getId(), thirdURL.getValue())).exists(),
                jsonPath("$.totalCount").value(3),
                jsonPath("$.listableCount").value(2)
            );

        this.mockMvc.perform(get("/url/list?start=-4&end=4"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpectAll(
                jsonPath("$.data").value(hasSize(2)),
                jsonPath("$.data[?(@.id == %d && @.value == '%s')]".formatted(secondURL.getId(), secondURL.getValue())).exists(),
                jsonPath("$.data[?(@.id == %d && @.value == '%s')]".formatted(thirdURL.getId(), thirdURL.getValue())).exists(),
                jsonPath("$.totalCount").value(3),
                jsonPath("$.listableCount").value(2)
            );

        this.mockMvc.perform(get("/url/list?start=-1&end=4"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpectAll(
                jsonPath("$.data").value(hasSize(2)),
                jsonPath("$.data[?(@.id == %d && @.value == '%s')]".formatted(secondURL.getId(), secondURL.getValue())).exists(),
                jsonPath("$.data[?(@.id == %d && @.value == '%s')]".formatted(thirdURL.getId(), thirdURL.getValue())).exists(),
                jsonPath("$.totalCount").value(3),
                jsonPath("$.listableCount").value(2)
            );
    }

    @Test
    void createURLWithInvalidListable() throws Exception {
        String content = "{\"value\": \"https://example.com/\", \"listable\": 22}";

        this.mockMvc.perform(post("/url").content(content).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpectAll(
                jsonPath("$.error").value("'listable' in data must be a boolean."),
                jsonPath("$.uri").value("/url")
            );
    }

    @Test
    void createValidURLForExistingId() throws Exception {
        when(this.random.generateRandomId()).thenReturn(
            1L, // for url
            2L, // for urlDeletion
            1L, // for url on loop
            1L, // for url on loop (finish)
            2L, // for urlDeletion on loop
            2L // for urlDeletion on loop (finish)
        );

        when(this.urlRepository.existsById(Long.toString(1L))).thenReturn(true, true, false);
        when(this.urlDeletionRepository.existsById(Long.toString(2L))).thenReturn(true, true, false);

        String value = "https://example.com/";
        String content = String.format("{\"value\": \"%s\"}", value);

        this.mockMvc.perform(post("/url").content(content).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpectAll(
                jsonPath("$.id").value(1L),
                jsonPath("$.value").value(value),
                jsonPath("$.clicks").value(0),
                jsonPath("$.createdAt").isString()
            );
    }

    @Test
    void goValidURL() throws Exception {
        String value = "https://example.com/";

        URLModel url = new URLModel(value, new Date(), false);
        url.setId(1L);
        url.setDeletionId(187L);

        when(urlRepository.findById(Long.toString(random.stringToId("1")))).thenReturn(Optional.of(url));

        this.mockMvc.perform(get("/url/go/1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(value));

        this.mockMvc.perform(get("/url/1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpectAll(
                jsonPath("$.id").value(1L),
                jsonPath("$.value").value(value),
                jsonPath("$.clicks").value(1),
                jsonPath("$.createdAt").isString()
            );
    }

    @Test
    void goInvalidURL() throws Exception {
        this.mockMvc.perform(get("/url/go/unknown"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().json("{\"error\": \"URL with id 'unknown' cannot be found.\", \"uri\": \"/url/go/unknown\"}"));
    }
}
