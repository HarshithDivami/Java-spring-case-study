package com.harshith.assigment.domain.prediction.repository;

import com.harshith.assigment.domain.prediction.entity.LeaguePredictionEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LeaguePredictionEntryRepository extends JpaRepository<LeaguePredictionEntry, UUID> {

    List<LeaguePredictionEntry> findByLeaguePredictionIdOrderByPosition(UUID predictionId);

    void deleteByLeaguePredictionId(UUID predictionId);
}
