package com.smartcampus.backend.service.weather;

public class AirQualityCodeResolver {

    public record AirQualityInfo(String category, String description, String icon) {}

    private static final AirQualityInfo UNKNOWN =
            new AirQualityInfo("Inconnu", "Donnee indisponible", "⚪");

    public static AirQualityInfo resolve(Integer europeanAqi) {
        if (europeanAqi == null) return UNKNOWN;

        if (europeanAqi <= 20) {
            return new AirQualityInfo("Bonne", "Qualite de l'air bonne, aucun risque pour la sante.", "🟢");
        } else if (europeanAqi <= 40) {
            return new AirQualityInfo("Correcte", "Qualite de l'air correcte, risque faible.", "🟡");
        } else if (europeanAqi <= 60) {
            return new AirQualityInfo("Moyenne", "Qualite moyenne, les personnes sensibles peuvent ressentir une gene.", "🟠");
        } else if (europeanAqi <= 80) {
            return new AirQualityInfo("Mauvaise", "Mauvaise qualite de l'air, effets possibles sur la sante de tous.", "🔴");
        } else if (europeanAqi <= 100) {
            return new AirQualityInfo("Tres mauvaise", "Tres mauvaise qualite de l'air, risques accrus pour la sante.", "🟣");
        } else {
            return new AirQualityInfo("Extremement mauvaise", "Qualite de l'air extremement mauvaise, danger pour la sante.", "🟤");
        }
    }
}