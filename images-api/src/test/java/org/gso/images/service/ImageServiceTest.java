package org.gso.images.service;

import org.gso.images.dto.ImageResponseDto;
import org.gso.images.model.ImageModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(imageService, "imgproxyUrl", "http://imgproxy:8080");
        ReflectionTestUtils.setField(imageService, "imgproxyKey", "test-key");
        ReflectionTestUtils.setField(imageService, "imgproxySalt", "test-salt");
    }

    @Test
    void testGetRandomImage_Success() {
        ImageResponseDto result = imageService.getRandomImage(null, null, false);

        assertNotNull(result);
        assertNotNull(result.getUrl());
        assertNotNull(result.getTitle());
        assertNotNull(result.getDescription());
        assertNull(result.getResizedUrl());
    }

    @Test
    void testGetRandomImage_WithResizing() {
        ImageResponseDto result = imageService.getRandomImage(800, 600, false);

        assertNotNull(result);
        assertNotNull(result.getUrl());
        assertNotNull(result.getResizedUrl());
        assertTrue(result.getResizedUrl().contains("imgproxy"));
        assertTrue(result.getResizedUrl().contains("800"));
        assertTrue(result.getResizedUrl().contains("600"));
    }

    @Test
    void testGetRandomImage_InvalidWidth_TooSmall() {
        assertThrows(IllegalArgumentException.class, () ->
                imageService.getRandomImage(50, 600, false)
        );
    }

    @Test
    void testGetRandomImage_InvalidWidth_TooLarge() {
        assertThrows(IllegalArgumentException.class, () ->
                imageService.getRandomImage(3000, 600, false)
        );
    }

    @Test
    void testGetRandomImage_InvalidHeight_TooSmall() {
        assertThrows(IllegalArgumentException.class, () ->
                imageService.getRandomImage(800, 50, false)
        );
    }

    @Test
    void testGetRandomImage_InvalidHeight_TooLarge() {
        assertThrows(IllegalArgumentException.class, () ->
                imageService.getRandomImage(800, 3000, false)
        );
    }

    @Test
    void testGetRandomImage_MinDimensions() {
        ImageResponseDto result = imageService.getRandomImage(100, 100, false);

        assertNotNull(result);
        assertNotNull(result.getResizedUrl());
        assertTrue(result.getResizedUrl().contains("100"));
    }

    @Test
    void testGetRandomImage_MaxDimensions() {
        ImageResponseDto result = imageService.getRandomImage(2000, 2000, false);

        assertNotNull(result);
        assertNotNull(result.getResizedUrl());
        assertTrue(result.getResizedUrl().contains("2000"));
    }

    @Test
    void testGetImageByUrl_Success() {
        String testUrl = "https://example.com/image.jpg";
        ImageResponseDto result = imageService.getImageByUrl(testUrl, 800, 600);

        assertNotNull(result);
        assertEquals(testUrl, result.getUrl());
        assertNotNull(result.getResizedUrl());
        assertTrue(result.getResizedUrl().contains("imgproxy"));
    }

    @Test
    void testGetImageByUrl_NoResizing() {
        String testUrl = "https://example.com/image.jpg";
        ImageResponseDto result = imageService.getImageByUrl(testUrl, null, null);

        assertNotNull(result);
        assertEquals(testUrl, result.getUrl());
        assertNull(result.getResizedUrl());
    }

    @Test
    void testGetAllImages_Success() {
        List<ImageModel> allImages = imageService.getAllImages();

        assertNotNull(allImages);
        assertFalse(allImages.isEmpty());
        assertEquals(8, allImages.size());
    }

    @Test
    void testGetAllImages_ContainsValidImages() {
        List<ImageModel> allImages = imageService.getAllImages();

        for (ImageModel image : allImages) {
            assertNotNull(image.getUrl());
            assertTrue(image.getUrl().startsWith("https://"));
            assertNotNull(image.getTitle());
            assertNotNull(image.getDescription());
        }
    }

    @Test
    void testImgproxyUrlFormat() {
        ImageResponseDto result = imageService.getRandomImage(800, 600, false);

        assertNotNull(result.getResizedUrl());
        assertTrue(result.getResizedUrl().matches("http://imgproxy:8080/resize/\\d+/\\d+/\\d+/\\d+/.+"));
    }
}
