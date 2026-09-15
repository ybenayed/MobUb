package com.smartcampus.backend.dto.search;

public class SearchResultDTO {

    private String name;
    private String subtitle;
    private Double latitude;
    private Double longitude;

    public SearchResultDTO() {
    }

    public SearchResultDTO(String name, String subtitle, double latitude, double longitude) {
        this.name = name;
        this.subtitle = subtitle;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}