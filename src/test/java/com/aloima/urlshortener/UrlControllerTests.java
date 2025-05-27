package com.aloima.urlshortener;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.aloima.urlshortener.component.RandomIdGenerator;
import com.aloima.urlshortener.model.URLModel;
import com.aloima.urlshortener.repository.URLDeletionRepository;
import com.aloima.urlshortener.repository.URLRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    void invalidURL() throws Exception {
        this.mockMvc.perform(get("/url/none"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().json("{\"error\": \"URL with id 'none' cannot be found.\", \"uri\": \"/url/none\"}"));
    }

    @Test
    void validURL() throws Exception {
        when(random.generateRandomId()).thenReturn("ranUrl");

        MockHttpServletResponse response = this.mockMvc.perform(post("/url").content("https://example.com/"))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpectAll(
                jsonPath("$.id").value("ranUrl"),
                jsonPath("$.value").value("https://example.com/"),
                jsonPath("$.clicks").value(0),
                jsonPath("$.createdAt").isString()
            )
            .andReturn().getResponse();

        String responseContent = response.getContentAsString();
        URLModel url = (new ObjectMapper()).readValue(responseContent, URLModel.class);

        when(urlRepository.findById(url.getId())).thenReturn(Optional.of(url));

        this.mockMvc.perform(get("/url/" + url.getId()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().json(responseContent));
    }
}
