package org.gso.citations.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.gso.citations.model.CitationModel;

import java.time.LocalDateTime;

public record CitationResponseDto(
        String id,
        String text,
        String author,
        String submittedByUsername,
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime submissionDate,
        String validatedByUsername,
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime validationDate,
        String status) {

    public static CitationResponseDto fromModel(CitationModel model) {
        return new CitationResponseDto(
                model.getId(),
                model.getText(),
                model.getAuthor(),
                model.getSubmittedByUsername(),
                model.getSubmissionDate(),
                model.getValidatedByUsername(),
                model.getValidationDate(),
                model.getStatus().toString());
    }
}
