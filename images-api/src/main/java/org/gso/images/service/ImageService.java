package org.gso.images.service;

import lombok.RequiredArgsConstructor;
import org.gso.images.dto.ImageResponseDto;
import org.gso.images.model.ImageModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class ImageService {

    @Value("${imgproxy.url:http://imgproxy:8080}")
    private String imgproxyUrl;

    private static final List<ImageModel> STATIC_IMAGES = Arrays.asList(
            new ImageModel(null, "https://picsum.photos/1200/800?random=1", "Random Image 1", "Lorem ipsum dolor sit amet"),
            new ImageModel(null, "https://picsum.photos/1200/800?random=2", "Random Image 2", "Consectetur adipiscing elit"),
            new ImageModel(null, "https://picsum.photos/1200/800?random=3", "Random Image 3", "Sed do eiusmod tempor"),
            new ImageModel(null, "https://picsum.photos/1200/800?random=4", "Random Image 4", "Incididunt ut labore et dolore"),
            new ImageModel(null, "https://picsum.photos/1200/800?random=5", "Random Image 5", "Magna aliqua ut enim"),
            new ImageModel(null, "https://picsum.photos/1200/800?random=6", "Random Image 6", "Ad minim veniam quis"),
            new ImageModel(null, "https://picsum.photos/1200/800?random=7", "Random Image 7", "Nostrud exercitation ullamco"),
            new ImageModel(null, "https://picsum.photos/1200/800?random=8", "Random Image 8", "Laboris nisi ut aliquip")
    );

    public ImageResponseDto getRandomImage(Integer width, Integer height, Boolean redirect) {
        validateDimensions(width, height);
        int randomIndex = ThreadLocalRandom.current().nextInt(STATIC_IMAGES.size());
        ImageModel image = STATIC_IMAGES.get(randomIndex);

        String resizedUrl = null;
        if (width != null || height != null) {
            resizedUrl = buildImgproxyUrl(image.getUrl(), width, height);
        }
        return new ImageResponseDto(
                image.getId(),
                image.getUrl(),
                resizedUrl,
                image.getTitle(),
                image.getDescription()
        );
    }

    private String buildImgproxyUrl(String imageUrl, Integer width, Integer height) {
        int w = width != null ? width : 800;
        int h = height != null ? height : 600;
        String encodedUrl = Base64.getUrlEncoder().withoutPadding().encodeToString(imageUrl.getBytes());
        return String.format("%s/resize/%d/%d/0/0/%s", imgproxyUrl, w, h, encodedUrl);
    }

    private void validateDimensions(Integer width, Integer height) {
        if (width != null && (width < 100 || width > 2000)) {
            throw new IllegalArgumentException("Width must be between 100 and 2000");
        }
        if (height != null && (height < 100 || height > 2000)) {
            throw new IllegalArgumentException("Height must be between 100 and 2000");
        }
    }

    public ImageResponseDto getImageByUrl(String url, Integer width, Integer height) {
        String resizedUrl = null;
        if (width != null || height != null) {
            resizedUrl = buildImgproxyUrl(url, width, height);
        }

        return new ImageResponseDto(
                null,
                url,
                resizedUrl,
                null,
                null
        );
    }

    public List<ImageModel> getAllImages() {
        return STATIC_IMAGES;
    }
}
