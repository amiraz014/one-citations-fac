package org.gso.images.controller;

import org.gso.images.service.ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImageController.class)
public class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageService imageService;

    @Test
    public void testGetRandomImage_Success() throws Exception {
        mockMvc.perform(get("/api/v1/images/random")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetRandomImage_WithDimensions() throws Exception {
        mockMvc.perform(get("/api/v1/images/random")
                .param("width", "800")
                .param("height", "600")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testResizeImage_Success() throws Exception {
        mockMvc.perform(get("/api/v1/images/resize")
                .param("url", "https://example.com/image.jpg")
                .param("width", "800")
                .param("height", "600")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testListImages_Success() throws Exception {
        mockMvc.perform(get("/api/v1/images/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testHealth_Success() throws Exception {
        mockMvc.perform(get("/api/v1/images/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }
}
