package org.gso.citations.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gso.citations.dto.CitationResponseDto;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "citations")
@NoArgsConstructor
@AllArgsConstructor
public class CitationModel {

    @Id
    private String id;
    private String text;
    private String author;
    private String submittedBy;
    private String submittedByUsername;
    private LocalDateTime submissionDate;
    private String validatedBy;
    private String validatedByUsername;
    private LocalDateTime validationDate;
    private CitationStatus status;

    public CitationModel(String text, String author, String submittedBy, String submittedByUsername) {
        this.text = text;
        this.author = author;
        this.submittedBy = submittedBy;
        this.submittedByUsername = submittedByUsername;
        this.submissionDate = LocalDateTime.now();
        this.status = CitationStatus.PENDING;
    }

    public CitationResponseDto toDto() {
        return new CitationResponseDto(
                this.id,
                this.text,
                this.author,
                this.submittedByUsername,
                this.submissionDate,
                this.validatedByUsername,
                this.validationDate,
                this.status.toString()
        );
    }

    public enum CitationStatus {
        PENDING, VALIDATED
    }
}
