package org.gso.citations.repository;

import org.gso.citations.model.CitationModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CitationRepository extends MongoRepository<CitationModel, String> {

    List<CitationModel> findByStatus(CitationModel.CitationStatus status);

    @Query("{ 'status' : 'PENDING' }")
    List<CitationModel> findPending();

    @Query("{ 'status' : 'VALIDATED' }")
    List<CitationModel> findValidated();

    @Override
    Optional<CitationModel> findById(String id);
}
