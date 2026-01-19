package org.gso.citations.service;

import org.gso.citations.dto.CitationResponseDto;
import org.gso.citations.dto.SubmitCitationDto;
import org.gso.citations.model.CitationModel;
import org.gso.citations.repository.CitationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitationServiceTest {

    @Mock
    private CitationRepository citationRepository;

    @InjectMocks
    private CitationService citationService;

    @Mock
    private Jwt jwt;

    private CitationModel validatedCitation;
    private CitationModel pendingCitation;

    @BeforeEach
    void setUp() {
        validatedCitation = CitationModel.builder()
                .id("1")
                .text("Être ou ne pas être")
                .author("Shakespeare")
                .submittedBy("user1")
                .submittedByUsername("john")
                .submissionDate(LocalDateTime.now())
                .validatedBy("moderator1")
                .validatedByUsername("jane")
                .validationDate(LocalDateTime.now())
                .status(CitationModel.CitationStatus.VALIDATED)
                .build();

        pendingCitation = CitationModel.builder()
                .id("2")
                .text("À être déterminé")
                .author("Inconnu")
                .submittedBy("user2")
                .submittedByUsername("bob")
                .submissionDate(LocalDateTime.now())
                .status(CitationModel.CitationStatus.PENDING)
                .build();
    }

    @Test
    void testGetRandomValidated_Success() {
        // Arrange
        List<CitationModel> validatedCitations = Arrays.asList(validatedCitation);
        when(citationRepository.findValidated()).thenReturn(validatedCitations);

        // Act
        var result = citationService.getRandomValidated();

        // Assert
        assertTrue(result.isPresent());
        assertEquals("1", result.get().getId());
        assertEquals("Être ou ne pas être", result.get().getText());
        verify(citationRepository, times(1)).findValidated();
    }

    @Test
    void testGetRandomValidated_NoValidatedCitations() {
        // Arrange
        when(citationRepository.findValidated()).thenReturn(Arrays.asList());

        // Act
        var result = citationService.getRandomValidated();

        // Assert
        assertTrue(result.isEmpty());
        verify(citationRepository, times(1)).findValidated();
    }

    @Test
    void testSubmitCitation_Success() {
        // Arrange
        SubmitCitationDto submitDto = new SubmitCitationDto("Une nouvelle citation", "Auteur");
        when(jwt.getSubject()).thenReturn("user123");
        when(jwt.getClaimAsString("preferred_username")).thenReturn("alice");

        CitationModel savedCitation = CitationModel.builder()
                .id("3")
                .text("Une nouvelle citation")
                .author("Auteur")
                .submittedBy("user123")
                .submittedByUsername("alice")
                .submissionDate(LocalDateTime.now())
                .status(CitationModel.CitationStatus.PENDING)
                .build();

        when(citationRepository.save(any(CitationModel.class))).thenReturn(savedCitation);

        // Act
        CitationResponseDto result = citationService.submitCitation(submitDto, jwt);

        // Assert
        assertNotNull(result);
        assertEquals("3", result.getId());
        assertEquals("Une nouvelle citation", result.getText());
        assertEquals("alice", result.getSubmittedByUsername());
        assertEquals(CitationModel.CitationStatus.PENDING, result.getStatus());
        verify(citationRepository, times(1)).save(any(CitationModel.class));
    }

    @Test
    void testGetPendingCitations_Success() {
        // Arrange
        List<CitationModel> pendingCitations = Arrays.asList(pendingCitation);
        when(citationRepository.findPending()).thenReturn(pendingCitations);

        // Act
        List<CitationResponseDto> results = citationService.getPendingCitations();

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("2", results.get(0).getId());
        assertEquals(CitationModel.CitationStatus.PENDING, results.get(0).getStatus());
        verify(citationRepository, times(1)).findPending();
    }

    @Test
    void testValidateCitation_Success() {
        // Arrange
        when(jwt.getSubject()).thenReturn("moderator123");
        when(jwt.getClaimAsString("preferred_username")).thenReturn("moderator");
        when(citationRepository.findById("2")).thenReturn(Optional.of(pendingCitation));

        CitationModel validatedByMod = CitationModel.builder()
                .id("2")
                .text("À être déterminé")
                .author("Inconnu")
                .submittedBy("user2")
                .submittedByUsername("bob")
                .submissionDate(LocalDateTime.now())
                .validatedBy("moderator123")
                .validatedByUsername("moderator")
                .validationDate(LocalDateTime.now())
                .status(CitationModel.CitationStatus.VALIDATED)
                .build();

        when(citationRepository.save(any(CitationModel.class))).thenReturn(validatedByMod);

        // Act
        CitationResponseDto result = citationService.validateCitation("2", jwt);

        // Assert
        assertNotNull(result);
        assertEquals("2", result.getId());
        assertEquals("moderator", result.getValidatedByUsername());
        assertEquals(CitationModel.CitationStatus.VALIDATED, result.getStatus());
        verify(citationRepository, times(1)).findById("2");
        verify(citationRepository, times(1)).save(any(CitationModel.class));
    }

    @Test
    void testValidateCitation_NotFound() {
        // Arrange
        when(citationRepository.findById("999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                citationService.validateCitation("999", jwt)
        );
        verify(citationRepository, times(1)).findById("999");
    }

    @Test
    void testValidateCitation_AlreadyValidated() {
        // Arrange
        when(citationRepository.findById("1")).thenReturn(Optional.of(validatedCitation));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                citationService.validateCitation("1", jwt)
        );
        verify(citationRepository, times(1)).findById("1");
    }

    @Test
    void testGetCitationById_Success() {
        // Arrange
        when(citationRepository.findById("1")).thenReturn(Optional.of(validatedCitation));

        // Act
        CitationResponseDto result = citationService.getCitationById("1");

        // Assert
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Être ou ne pas être", result.getText());
        verify(citationRepository, times(1)).findById("1");
    }

    @Test
    void testGetCitationById_NotFound() {
        // Arrange
        when(citationRepository.findById("999")).thenReturn(Optional.empty());

        // Act
        CitationResponseDto result = citationService.getCitationById("999");

        // Assert
        assertNull(result);
        verify(citationRepository, times(1)).findById("999");
    }
}
