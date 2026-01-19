package org.gso.images.controller;

import lombok.RequiredArgsConstructor;
import org.gso.images.dto.ImageResponseDto;
import org.gso.images.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/random")
    public ResponseEntity<ImageResponseDto> getRandomImage(
            @RequestParam(required = false) Integer width,
            @RequestParam(required = false) Integer height,
            @RequestParam(defaultValue = "false") Boolean redirect) {
        
        try {
            ImageResponseDto image = imageService.getRandomImage(width, height, redirect);
            if (redirect && image.resizedUrl() != null) {
                return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                        .header("Location", image.resizedUrl())
                        .build();
            }
            
            return ResponseEntity.ok(image);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/resize")
    public ResponseEntity<ImageResponseDto> resizeImage(
            @RequestParam String url,
            @RequestParam(required = false) Integer width,
            @RequestParam(required = false) Integer height) {
        
        try {
            ImageResponseDto image = imageService.getImageByUrl(url, width, height);
            return ResponseEntity.ok(image);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> listImages() {
        return ResponseEntity.ok(imageService.getAllImages());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
