package com.smartcampus.backend.dto.search;

import java.util.List;

public class ItineraryOptionDTO {
    private Long duration;
    private Integer transfers;
    private Double walkDistance;
    private Double co2Grams;
    private Double accessibilityScore; 
    private String profileLabel;
    private List<LegDTO> legs;

    public ItineraryOptionDTO() {
    }

    public ItineraryOptionDTO(Long duration, Integer transfers, Double walkDistance,
                               Double co2Grams, Double accessibilityScore,
                               String profileLabel, List<LegDTO> legs) {
        this.duration = duration;
        this.transfers = transfers;
        this.walkDistance = walkDistance;
        this.co2Grams = co2Grams;
        this.accessibilityScore = accessibilityScore;
        this.profileLabel = profileLabel;
        this.legs = legs;
    }

    public Long getDuration() { return duration; }
    public void setDuration(Long duration) { this.duration = duration; }

    public Integer getTransfers() { return transfers; }
    public void setTransfers(Integer transfers) { this.transfers = transfers; }

    public Double getWalkDistance() { return walkDistance; }
    public void setWalkDistance(Double walkDistance) { this.walkDistance = walkDistance; }

    public Double getCo2Grams() { return co2Grams; }
    public void setCo2Grams(Double co2Grams) { this.co2Grams = co2Grams; }

    public Double getAccessibilityScore() { return accessibilityScore; }
    public void setAccessibilityScore(Double accessibilityScore) { this.accessibilityScore = accessibilityScore; }

    public String getProfileLabel() { return profileLabel; }
    public void setProfileLabel(String profileLabel) { this.profileLabel = profileLabel; }

    public List<LegDTO> getLegs() { return legs; }
    public void setLegs(List<LegDTO> legs) { this.legs = legs; }
}