package com.aloima.urlshortener;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        URLModel url = new URLModel("https://example.com/", new Date());
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
        URLModel url = new URLModel("https://example.com/", new Date());
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

        this.mockMvc.perform(post("/url").content(value))
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

        this.mockMvc.perform(post("/url").content(value))
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
}
