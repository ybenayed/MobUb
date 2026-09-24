package com.smartcampus.backend.mapper.search;

import com.smartcampus.backend.dto.search.LegDTO;
import com.smartcampus.backend.dto.search.SearchResultDTO;
import com.smartcampus.backend.dto.search.history.SaveSearchHistoryRequestDTO;
import com.smartcampus.backend.dto.search.history.SearchHistoryLegResponseDTO;
import com.smartcampus.backend.dto.search.history.SearchHistoryResponseDTO;
import com.smartcampus.backend.entity.User;
import com.smartcampus.backend.entity.search.PlaceRef;
import com.smartcampus.backend.entity.search.SearchHistory;
import com.smartcampus.backend.entity.search.SearchHistoryLeg;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
public class SearchHistoryMapper {

    private static final ZoneId PARIS_ZONE = ZoneId.of("Europe/Paris");

    public SearchHistory toEntity(User user, SaveSearchHistoryRequestDTO request) {
        var itinerary = request.getItinerary();
        var legDTOs = itinerary != null ? itinerary.getLegs() : null;

        SearchHistory history = SearchHistory.builder()
                .user(user)
                .origin(toPlaceRef(request.getOrigin()))
                .destination(toPlaceRef(request.getDestination()))
                .departureTime(firstStartTime(legDTOs))
                .arrivalTime(lastEndTime(legDTOs))
                .duration(itinerary != null && itinerary.getDuration() != null ? itinerary.getDuration() : 0L)
                .numberOfTransfers(itinerary != null && itinerary.getTransfers() != null ? itinerary.getTransfers() : 0)
                .walkDistance(itinerary != null ? itinerary.getWalkDistance() : null)
                .accessibilityScore(itinerary != null ? itinerary.getAccessibilityScore() : null)
                .co2Grams(itinerary != null ? itinerary.getCo2Grams() : null)
                .profileLabel(itinerary != null ? itinerary.getProfileLabel() : null)
                .build();

        if (legDTOs != null) {
            int order = 0;
            for (LegDTO legDTO : legDTOs) {
                history.addLeg(toLegEntity(legDTO, order++));
            }
        }
        return history;
    }

    public SearchHistoryResponseDTO toResponseDTO(SearchHistory entity) {
        List<SearchHistoryLegResponseDTO> legDTOs = new ArrayList<>();
        if (entity.getLegs() != null) {
            for (SearchHistoryLeg leg : entity.getLegs()) {
                legDTOs.add(SearchHistoryLegResponseDTO.builder()
                    .mode(leg.getMode())
                    .fromName(leg.getFrom() != null ? leg.getFrom().getName() : null)
                    .fromLat(leg.getFrom() != null && leg.getFrom().getLat() != null ? leg.getFrom().getLat() : 0.0)
                    .fromLon(leg.getFrom() != null && leg.getFrom().getLon() != null ? leg.getFrom().getLon() : 0.0)
                    .toName(leg.getTo() != null ? leg.getTo().getName() : null)
                    .toLat(leg.getTo() != null && leg.getTo().getLat() != null ? leg.getTo().getLat() : 0.0)
                    .toLon(leg.getTo() != null && leg.getTo().getLon() != null ? leg.getTo().getLon() : 0.0)
                    .distance(leg.getDistance())
                    .rentedBike(leg.getRentedBike())
                    .routeName(leg.getRouteName())
                    .startTime(leg.getStartTime())
                    .endTime(leg.getEndTime())
                    .build());
            }
        }

        return SearchHistoryResponseDTO.builder()
                .id(entity.getId())
                .originName(entity.getOrigin() != null ? entity.getOrigin().getName() : null)
                .destinationName(entity.getDestination() != null ? entity.getDestination().getName() : null)
                .departureTime(entity.getDepartureTime())
                .arrivalTime(entity.getArrivalTime())
                .duration(entity.getDuration())
                .numberOfTransfers(entity.getNumberOfTransfers())
                .walkDistance(entity.getWalkDistance())
                .accessibilityScore(entity.getAccessibilityScore())
                .co2Grams(entity.getCo2Grams())
                .profileLabel(entity.getProfileLabel())
                .searchedAt(entity.getSearchedAt())
                .legs(legDTOs)
                .build();
    }


    private PlaceRef toPlaceRef(SearchResultDTO dto) {
        if (dto == null) return null;
        return PlaceRef.builder()
                .name(dto.getName())
                .lat(dto.getLatitude())
                .lon(dto.getLongitude())
                .build();
    }

    private SearchHistoryLeg toLegEntity(LegDTO legDTO, int order) {
        return SearchHistoryLeg.builder()
                .sequenceOrder(order)
                .mode(legDTO != null ? legDTO.getMode() : null)
                .distance(legDTO != null ? legDTO.getDistance() : null)
                .rentedBike(legDTO != null ? legDTO.getRentedBike() : null)
                .from(legDTO != null ? PlaceRef.builder()
                        .name(legDTO.getFromName())
                        .lat(legDTO.getFromLat())
                        .lon(legDTO.getFromLon())
                        .build() : null)
                .to(legDTO != null ? PlaceRef.builder()
                        .name(legDTO.getToName())
                        .lat(legDTO.getToLat())
                        .lon(legDTO.getToLon())
                        .build() : null)
                .routeName(legDTO != null ? legDTO.getRouteName() : null)
                .startTime(legDTO != null ? epochMillisToLocalDateTime(legDTO.getStartTime()) : null)
                .endTime(legDTO != null ? epochMillisToLocalDateTime(legDTO.getEndTime()) : null)
                .build();
    }

    private LocalDateTime epochMillisToLocalDateTime(Long epochMillis) {
        if (epochMillis == null || epochMillis <= 0) return null;
        return Instant.ofEpochMilli(epochMillis).atZone(PARIS_ZONE).toLocalDateTime();
    }

    private LocalDateTime firstStartTime(List<LegDTO> legs) {
        if (legs == null || legs.isEmpty()) return null;
        return epochMillisToLocalDateTime(legs.get(0).getStartTime());
    }

    private LocalDateTime lastEndTime(List<LegDTO> legs) {
        if (legs == null || legs.isEmpty()) return null;
        return epochMillisToLocalDateTime(legs.get(legs.size() - 1).getEndTime());
    }
}