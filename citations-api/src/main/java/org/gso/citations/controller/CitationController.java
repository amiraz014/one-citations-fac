package org.gso.citations.controller;

import lombok.RequiredArgsConstructor;
import org.gso.citations.dto.CitationResponseDto;
import org.gso.citations.dto.SubmitCitationDto;
import org.gso.citations.service.CitationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/citations")
@RequiredArgsConstructor
public class CitationController {

    private final CitationService citationService;

    @GetMapping("/random")
    public ResponseEntity<CitationResponseDto> getRandomCitation() {
        return citationService.getRandomValidated()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('writer')")
    public ResponseEntity<CitationResponseDto> submitCitation(
            @RequestBody SubmitCitationDto dto) {
        Jwt jwtToken = extractJwt();
        CitationResponseDto response = citationService.submitCitation(dto, jwtToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('moderator')")
    public ResponseEntity<List<CitationResponseDto>> getPendingCitations() {
        List<CitationResponseDto> pending = citationService.getPendingCitations();
        return ResponseEntity.ok(pending);
    }

    @PutMapping("/{id}/validate")
    @PreAuthorize("hasRole('moderator')")
    public ResponseEntity<CitationResponseDto> validateCitation(@PathVariable String id) {
        Jwt jwtToken = extractJwt();
        CitationResponseDto response = citationService.validateCitation(id, jwtToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitationResponseDto> getCitationById(@PathVariable String id) {
        return citationService.getCitationById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private Jwt extractJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt)) {
            throw new IllegalStateException("JWT token not found in security context");
        }
        return (Jwt) auth.getPrincipal();
    }
}

