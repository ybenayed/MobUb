package com.smartcampus.backend.entity.search;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Un segment (leg) d'un itineraire sauvegarde : marche, bus, velo...
 * Miroir persistant de LegDTO. sequenceOrder garde l'ordre du trajet
 * (WALK -> BUS -> WALK), indispensable car une List JPA sans @OrderColumn
 * ne garantit pas l'ordre de retour.
 */
@Entity
@Table(name = "search_history_leg")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchHistoryLeg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "search_history_id", nullable = false)
    private SearchHistory searchHistory;

    @Column(name = "sequence_order", nullable = false)
    private int sequenceOrder;

    @Column(nullable = false, length = 30)
    private String mode;

    private Double distance;

    @Column(name = "rented_bike")
    private Boolean rentedBike;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "from_name")),
            @AttributeOverride(name = "lat", column = @Column(name = "from_lat")),
            @AttributeOverride(name = "lon", column = @Column(name = "from_lon"))
    })
    private PlaceRef from;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "to_name")),
            @AttributeOverride(name = "lat", column = @Column(name = "to_lat")),
            @AttributeOverride(name = "lon", column = @Column(name = "to_lon"))
    })
    private PlaceRef to;

    /** Nom court de la ligne (route.shortName), null si mode != transport en commun. */
    @Column(name = "route_name")
    private String routeName;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;
}