package com.smartcampus.backend.entity.search;

import com.smartcampus.backend.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Une recherche d'itineraire sauvegardee par un utilisateur.
 * Correspond a un ItineraryOptionDTO "choisi" par le client parmi les options
 */
@Entity
@Table(name = "search_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "origin_name")),
            @AttributeOverride(name = "lat", column = @Column(name = "origin_lat")),
            @AttributeOverride(name = "lon", column = @Column(name = "origin_lon"))
    })
    private PlaceRef origin;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "destination_name")),
            @AttributeOverride(name = "lat", column = @Column(name = "destination_lat")),
            @AttributeOverride(name = "lon", column = @Column(name = "destination_lon"))
    })
    private PlaceRef destination;

    @Column(name = "departure_time")
    private LocalDateTime departureTime;

    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;

    @Column(nullable = false)
    private long duration;

    @Column(name = "number_of_transfers", nullable = false)
    private int numberOfTransfers;

    @Column(name = "walk_distance")
    private Double walkDistance;

    @Column(name = "accessibility_score")
    private Double accessibilityScore;

    @Column(name = "co2_grams")
    private Double co2Grams;

    @Column(name = "profile_label")
    private String profileLabel;

    @CreationTimestamp
    @Column(name = "searched_at", updatable = false)
    private LocalDateTime searchedAt;

    @Builder.Default
    @OneToMany(mappedBy = "searchHistory", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequenceOrder ASC")
    private List<SearchHistoryLeg> legs = new ArrayList<>();

    public void addLeg(SearchHistoryLeg leg) {
        legs.add(leg);
        leg.setSearchHistory(this);
    }
}