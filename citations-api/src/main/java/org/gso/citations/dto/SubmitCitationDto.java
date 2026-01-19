package org.gso.citations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubmitCitationDto(
        @NotBlank(message = "Citation text cannot be blank")
        @Size(min = 10, max = 5000, message = "Citation text must be between 10 and 5000 characters")
        String text,
        
        @NotBlank(message = "Author cannot be blank")
        @Size(min = 2, max = 200, message = "Author must be between 2 and 200 characters")
        String author) {
}
