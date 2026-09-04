// ItineraryOptionDTO.java
package com.smartcampus.backend.dto.search;

import java.util.List;

/**
 * Une option d'itineraire proposee par OTP, enrichie de metriques
 * TOUTES fournies nativement par OTP desormais (nouveau schema planConnection) :
 *
 * - duration           : duree totale en secondes
 * - transfers          : nombre de correspondances (Itinerary.numberOfTransfers)
 * - walkDistance       : distance de marche totale en metres (Itinerary.walkDistance)
 * - co2Grams           : estimation CO2 grammes/personne (Itinerary.emissionsPerPerson.co2),
 *                        calculee par OTP lui-meme -> plus fiable qu'un calcul manuel
 * - accessibilityScore : 0.0 (non accessible) a 1.0 (accessible), utilise pour le tri PMR
 * - profileLabel       : optionnel, uniquement pour les variantes velo perso
 *                        (ex: "Vélo — le plus rapide"). Null pour les autres itineraires.
 */
public class ItineraryOptionDTO {
    private long duration;
    private int transfers;
    private double walkDistance;
    private double co2Grams;
    private double accessibilityScore;
    private String profileLabel;
    private List<LegDTO> legs;

    public ItineraryOptionDTO() {
    }

    public ItineraryOptionDTO(long duration, int transfers, double walkDistance,
                               double co2Grams, double accessibilityScore,
                               String profileLabel, List<LegDTO> legs) {
        this.duration = duration;
        this.transfers = transfers;
        this.walkDistance = walkDistance;
        this.co2Grams = co2Grams;
        this.accessibilityScore = accessibilityScore;
        this.profileLabel = profileLabel;
        this.legs = legs;
    }

    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }

    public int getTransfers() { return transfers; }
    public void setTransfers(int transfers) { this.transfers = transfers; }

    public double getWalkDistance() { return walkDistance; }
    public void setWalkDistance(double walkDistance) { this.walkDistance = walkDistance; }

    public double getCo2Grams() { return co2Grams; }
    public void setCo2Grams(double co2Grams) { this.co2Grams = co2Grams; }

    public double getAccessibilityScore() { return accessibilityScore; }
    public void setAccessibilityScore(double accessibilityScore) { this.accessibilityScore = accessibilityScore; }

    public String getProfileLabel() { return profileLabel; }
    public void setProfileLabel(String profileLabel) { this.profileLabel = profileLabel; }

    public List<LegDTO> getLegs() { return legs; }
    public void setLegs(List<LegDTO> legs) { this.legs = legs; }
}