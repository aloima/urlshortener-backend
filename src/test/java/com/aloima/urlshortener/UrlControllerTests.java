package com.aloima.urlshortener;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.aloima.urlshortener.controller.URLController;
import com.aloima.urlshortener.service.URLService;

@WebMvcTest(URLController.class)
class UrlControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private URLService urlService;

    @Test
    void invalidURL() throws Exception {
        this.mockMvc.perform(get("/url/none")).andDo(print()).andExpect(status().isNotFound())
            .andExpect(content().json("{\"error\": \"URL with id 'none' cannot be found.\", \"uri\": \"/url/none\"}"));
    }
}
