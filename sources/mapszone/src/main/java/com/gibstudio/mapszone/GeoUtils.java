package com.gibstudio.mapszone;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

public final class GeoUtils {

    public static final int INCORRECT_VALUE = -1;
    public static final double RADIUS_OF_EARTH_METERS = 6371009;

    public static LatLng toRadiusLatLng(LatLng centerPoint, double radius) {
        double radiusAngle = Math.toDegrees(radius / RADIUS_OF_EARTH_METERS) /
                Math.cos(Math.toRadians(centerPoint.latitude));
        return new LatLng(centerPoint.latitude, centerPoint.longitude + radiusAngle);
    }

    public static double toRadiusMeters(LatLng centerPoint, LatLng radiusPoint) {
        float[] result = new float[1];
        Location.distanceBetween(centerPoint.latitude, centerPoint.longitude,
                radiusPoint.latitude, radiusPoint.longitude, result);
        return result[0];
    }

    public static LatLngBounds getBounds(List<LatLng> points) {
        if (points != null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng point : points) {
                builder.include(point);
            }
            return builder.build();
        }
        return null;
    }

    public static LatLngBounds getBoundsForMarkers(List<Marker> radiusMarkers) {
        if (radiusMarkers != null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : radiusMarkers) {
                builder.include(marker.getPosition());
            }
            return builder.build();
        }
        return null;
    }

    public static double getBiggestDistanceFromCenter(LatLng centerPoint, List<LatLng> points) {
        if (points != null) {
            double radius = 0;
            for (LatLng point : points) {
                double distance = toRadiusMeters(centerPoint, point);
                if (radius < distance) {
                    radius = distance;
                }
            }
            return radius;
        }
        return 0;
    }

    public static double getBiggestDistanceFromCenter(List<LatLng> points) {
        if (points != null) {
            final LatLngBounds bounds = getBounds(points);
            return getBiggestDistanceFromCenter(bounds.getCenter(), points);
        }
        return 0;
    }

    public static double getBiggestDistanceFromCenterForMarkers(List<Marker> radiusMarkers) {
        if (radiusMarkers != null) {
            List<LatLng> points = new ArrayList<>(radiusMarkers.size());
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : radiusMarkers) {
                points.add(marker.getPosition());
                builder.include(marker.getPosition());
            }

            final LatLngBounds bounds = builder.build();
            return getBiggestDistanceFromCenter(bounds.getCenter(), points);
        }
        return 0;
    }

    public static double getBiggestDistanceFromCenterForMarkers(LatLng centerPoint,
                                                                List<Marker> radiusMarkers) {
        if (radiusMarkers != null) {
            List<LatLng> points = new ArrayList<>(radiusMarkers.size());
            for (Marker marker : radiusMarkers) {
                points.add(marker.getPosition());
            }

            return getBiggestDistanceFromCenter(centerPoint, points);
        }
        return 0;
    }

    public static List<LatLng> getPointsForMarkers(List<Marker> radiusMarkers) {
        if (radiusMarkers != null) {
            ArrayList<LatLng> points = new ArrayList<>(radiusMarkers.size());
            for (Marker marker : radiusMarkers) {
                points.add(marker.getPosition());
            }
            return points;
        }
        return null;
    }

    public static List<LatLng> convertCircleToPolygon(LatLng centerPoint, double radius) {
        ArrayList<LatLng> points = new ArrayList<>();
        for (int heading = 0; heading < 360; heading += 15) {
            LatLng position = SphericalUtil.computeOffset(centerPoint, radius, heading);
            points.add(position);
        }
        return points;
    }

    public static List<LatLng> convertCircleToPolygon(LatLng centerPoint, LatLng radiusPoint) {
        final double radius = GeoUtils.toRadiusMeters(centerPoint, radiusPoint);
        ArrayList<LatLng> points = new ArrayList<>();
        for (int heading = 0; heading < 360; heading += 15) {
            LatLng position = SphericalUtil.computeOffset(centerPoint, radius, heading);
            points.add(position);
        }
        return points;
    }
}
