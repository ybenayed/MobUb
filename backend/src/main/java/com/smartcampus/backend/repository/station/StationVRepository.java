package com.smartcampus.backend.repository.station;

import com.smartcampus.backend.dto.station.StationPositionVDTO;
import com.smartcampus.backend.entity.station.StationV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StationVRepository extends JpaRepository<StationV, Long> {

    Optional<StationV> findByStationId(String stationId);
    boolean existsByStationId(String stationId);

    @Query("""
        SELECT new com.smartcampus.backend.dto.station.StationPositionVDTO(
            s.id, s.stationId, s.nom, s.latitude, s.longitude)
        FROM StationV s
        """)
    List<StationPositionVDTO> findAllPositions();
    List<StationV> findByNomContainingIgnoreCase(String nom);
}