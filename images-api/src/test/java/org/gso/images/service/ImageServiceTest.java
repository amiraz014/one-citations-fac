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
        // Set imgproxy properties via reflection
        ReflectionTestUtils.setField(imageService, "imgproxyUrl", "http://imgproxy:8080");
        ReflectionTestUtils.setField(imageService, "imgproxyKey", "test-key");
        ReflectionTestUtils.setField(imageService, "imgproxySalt", "test-salt");
    }

    @Test
    void testGetRandomImage_Success() {
        // Act
        ImageResponseDto result = imageService.getRandomImage(null, null, false);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getUrl());
        assertNotNull(result.getTitle());
        assertNotNull(result.getDescription());
        assertNull(result.getResizedUrl()); // No resizing params
    }

    @Test
    void testGetRandomImage_WithResizing() {
        // Act
        ImageResponseDto result = imageService.getRandomImage(800, 600, false);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getUrl());
        assertNotNull(result.getResizedUrl());
        assertTrue(result.getResizedUrl().contains("imgproxy"));
        assertTrue(result.getResizedUrl().contains("800"));
        assertTrue(result.getResizedUrl().contains("600"));
    }

    @Test
    void testGetRandomImage_InvalidWidth_TooSmall() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                imageService.getRandomImage(50, 600, false)
        );
    }

    @Test
    void testGetRandomImage_InvalidWidth_TooLarge() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                imageService.getRandomImage(3000, 600, false)
        );
    }

    @Test
    void testGetRandomImage_InvalidHeight_TooSmall() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                imageService.getRandomImage(800, 50, false)
        );
    }

    @Test
    void testGetRandomImage_InvalidHeight_TooLarge() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                imageService.getRandomImage(800, 3000, false)
        );
    }

    @Test
    void testGetRandomImage_MinDimensions() {
        // Act
        ImageResponseDto result = imageService.getRandomImage(100, 100, false);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getResizedUrl());
        assertTrue(result.getResizedUrl().contains("100"));
    }

    @Test
    void testGetRandomImage_MaxDimensions() {
        // Act
        ImageResponseDto result = imageService.getRandomImage(2000, 2000, false);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getResizedUrl());
        assertTrue(result.getResizedUrl().contains("2000"));
    }

    @Test
    void testGetImageByUrl_Success() {
        // Act
        String testUrl = "https://example.com/image.jpg";
        ImageResponseDto result = imageService.getImageByUrl(testUrl, 800, 600);

        // Assert
        assertNotNull(result);
        assertEquals(testUrl, result.getUrl());
        assertNotNull(result.getResizedUrl());
        assertTrue(result.getResizedUrl().contains("imgproxy"));
    }

    @Test
    void testGetImageByUrl_NoResizing() {
        // Act
        String testUrl = "https://example.com/image.jpg";
        ImageResponseDto result = imageService.getImageByUrl(testUrl, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(testUrl, result.getUrl());
        assertNull(result.getResizedUrl());
    }

    @Test
    void testGetAllImages_Success() {
        // Act
        List<ImageModel> allImages = imageService.getAllImages();

        // Assert
        assertNotNull(allImages);
        assertFalse(allImages.isEmpty());
        assertEquals(8, allImages.size()); // We have 8 static images
    }

    @Test
    void testGetAllImages_ContainsValidImages() {
        // Act
        List<ImageModel> allImages = imageService.getAllImages();

        // Assert
        for (ImageModel image : allImages) {
            assertNotNull(image.getUrl());
            assertTrue(image.getUrl().startsWith("https://"));
            assertNotNull(image.getTitle());
            assertNotNull(image.getDescription());
        }
    }

    @Test
    void testImgproxyUrlFormat() {
        // Act
        ImageResponseDto result = imageService.getRandomImage(800, 600, false);

        // Assert
        assertNotNull(result.getResizedUrl());
        // Format should be: http://imgproxy:8080/resize/800/600/0/0/ENCODED_URL
        assertTrue(result.getResizedUrl().matches("http://imgproxy:8080/resize/\\d+/\\d+/\\d+/\\d+/.+"));
    }
}
