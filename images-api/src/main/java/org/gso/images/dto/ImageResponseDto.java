package org.gso.images.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ImageResponseDto(
        String id,
        String url,
        String resizedUrl,
        String title,
        String description) {
}
