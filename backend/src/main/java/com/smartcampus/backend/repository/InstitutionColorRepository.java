package com.smartcampus.backend.repository;

import com.smartcampus.backend.entity.InstitutionColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstitutionColorRepository extends JpaRepository<InstitutionColor, Long> {
    Optional<InstitutionColor> findByInstitution(String institution);
    boolean existsByInstitution(String institution);
}