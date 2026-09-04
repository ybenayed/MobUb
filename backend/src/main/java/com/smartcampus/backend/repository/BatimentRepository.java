package com.smartcampus.backend.repository;

import com.smartcampus.backend.entity.Batiment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BatimentRepository extends JpaRepository<Batiment, Long> {
    Optional<Batiment> findByName(String name);
    List<Batiment> findByCampusId(Long campusId);
    boolean existsByCampusId(Long campusId);
    long countByCampusId(Long campusId);
    void deleteByCampusId(Long campusId);
}