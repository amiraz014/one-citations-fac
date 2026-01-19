package org.gso.citations.service;

import lombok.RequiredArgsConstructor;
import org.gso.citations.dto.SubmitCitationDto;
import org.gso.citations.dto.CitationResponseDto;
import org.gso.citations.model.CitationModel;
import org.gso.citations.repository.CitationRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CitationService {

    private final CitationRepository citationRepository;

    public Optional<CitationResponseDto> getRandomValidated() {
        List<CitationModel> validated = citationRepository.findValidated();
        if (validated.isEmpty()) {
            return Optional.empty();
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(validated.size());
        return Optional.of(CitationResponseDto.fromModel(validated.get(randomIndex)));
    }

    public CitationResponseDto submitCitation(SubmitCitationDto dto, Jwt jwt) {
        String userId = jwt.getSubject();
        String username = jwt.getClaimAsString("preferred_username");

        CitationModel citation = new CitationModel(
                dto.text(),
                dto.author(),
                userId,
                username
        );

        CitationModel saved = citationRepository.save(citation);
        return CitationResponseDto.fromModel(saved);
    }

    public List<CitationResponseDto> getPendingCitations() {
        return citationRepository.findPending()
                .stream()
                .map(CitationResponseDto::fromModel)
                .collect(Collectors.toList());
    }

    public CitationResponseDto validateCitation(String citationId, Jwt jwt) {
        CitationModel citation = citationRepository.findById(citationId)
                .orElseThrow(() -> new IllegalArgumentException("Citation not found with id: " + citationId));

        if (citation.getStatus() != CitationModel.CitationStatus.PENDING) {
            throw new IllegalStateException("Citation is not pending");
        }

        String moderatorId = jwt.getSubject();
        String moderatorUsername = jwt.getClaimAsString("preferred_username");

        citation.setValidatedBy(moderatorId);
        citation.setValidatedByUsername(moderatorUsername);
        citation.setValidationDate(LocalDateTime.now());
        citation.setStatus(CitationModel.CitationStatus.VALIDATED);

        CitationModel saved = citationRepository.save(citation);
        return CitationResponseDto.fromModel(saved);
    }

    public Optional<CitationResponseDto> getCitationById(String citationId) {
        return citationRepository.findById(citationId)
                .map(CitationResponseDto::fromModel);
    }
}
