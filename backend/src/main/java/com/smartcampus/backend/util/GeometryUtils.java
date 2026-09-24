package com.smartcampus.backend.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.List;


public final class GeometryUtils {

    private GeometryUtils() {
    }

    public static Geometry parseGeometry(JsonNode geometryNode, GeometryFactory gf) {
        String type = geometryNode.path("type").asText("");
        JsonNode coordinates = geometryNode.path("coordinates");

        Geometry geometry = switch (type) {
            case "Polygon" -> toPolygon(coordinates, gf);
            case "MultiPolygon" -> toMultiPolygon(coordinates, gf);
            default -> throw new IllegalArgumentException("Type de géométrie non supporté : " + type);
        };

        if (!geometry.isValid()) {
            geometry = geometry.buffer(0);
        }
        return geometry;
    }

    private static Polygon toPolygon(JsonNode polygonCoords, GeometryFactory gf) {
        JsonNode ring = polygonCoords.get(0); 
        List<Coordinate> coords = new ArrayList<>();
        for (JsonNode pt : ring) {
            coords.add(new Coordinate(pt.get(0).asDouble(), pt.get(1).asDouble()));
        }
        if (coords.size() < 3) {
            throw new IllegalArgumentException("Anneau incomplet (" + coords.size() + " points)");
        }
        if (!coords.get(0).equals2D(coords.get(coords.size() - 1))) {
            coords.add(new Coordinate(coords.get(0)));
        }
        LinearRing linearRing = gf.createLinearRing(coords.toArray(new Coordinate[0]));
        return gf.createPolygon(linearRing);
    }

    private static MultiPolygon toMultiPolygon(JsonNode multiCoords, GeometryFactory gf) {
        List<Polygon> polygons = new ArrayList<>();
        for (JsonNode polyCoords : multiCoords) {
            polygons.add(toPolygon(polyCoords, gf));
        }
        return gf.createMultiPolygon(polygons.toArray(new Polygon[0]));
    }

    public static Geometry union(List<Geometry> geometries) {
        Geometry result = geometries.get(0);
        for (int i = 1; i < geometries.size(); i++) {
            result = result.union(geometries.get(i));
        }
        return result;
    }

    public static double perimeterMeters(Geometry geometry) {
        double total = 0;
        for (int i = 0; i < geometry.getNumGeometries(); i++) {
            Geometry part = geometry.getGeometryN(i);
            if (part instanceof Polygon polygon) {
                total += ringLength(polygon.getExteriorRing().getCoordinates());
            }
        }
        return total;
    }

    public static List<List<double[]>> extractExteriorRings(Geometry geometry) {
        List<List<double[]>> parts = new ArrayList<>();
        if (geometry == null) {
            return parts;
        }
        for (int i = 0; i < geometry.getNumGeometries(); i++) {
            Geometry part = geometry.getGeometryN(i);
            if (part instanceof Polygon polygon) {
                List<double[]> ring = new ArrayList<>();
                for (Coordinate c : polygon.getExteriorRing().getCoordinates()) {
                    ring.add(new double[]{c.getX(), c.getY()});
                }
                parts.add(ring);
            }
        }
        return parts;
    }

    private static double ringLength(Coordinate[] coords) {
        double total = 0;
        for (int i = 0; i < coords.length - 1; i++) {
            total += haversine(coords[i].getY(), coords[i].getX(), coords[i + 1].getY(), coords[i + 1].getX());
        }
        return total;
    }

    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}