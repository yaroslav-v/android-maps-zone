package com.gibstudio.mapszone.models;

import android.graphics.Color;

import com.gibstudio.mapszone.GeoUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

public class DraggablePolygon implements DraggableShape {

    private final Marker mCenterMarker;
    private final List<Marker> mRadiusMarkers;
    private final Polygon mPolygon;

    private BitmapDescriptor mCenterResource;
    private BitmapDescriptor mCenterEditingResource;

    private long mId = GeoUtils.INCORRECT_VALUE;

    private LatLng mCenterPoint;
    private List<LatLng> mRadiusPoints;

    private LatLng mCurrentCenterPoint; // used only for correct moving of shape

    public DraggablePolygon(GoogleMap map, List<LatLng> radiusPoints, int strokeColor,
                            int fillColor, float strokeWidth, BitmapDescriptor centerResource,
                            BitmapDescriptor centerEditingResource, BitmapDescriptor radiusResource) {
        final boolean draggable = true;

        LatLngBounds bounds = GeoUtils.getBounds(radiusPoints);
        mCenterPoint = bounds.getCenter();
        mRadiusPoints = radiusPoints;

        mCurrentCenterPoint = mCenterPoint;

        mCenterResource = centerResource;
        mCenterEditingResource = centerEditingResource;

        mCenterMarker = map.addMarker(new MarkerOptions()
                .position(mCenterPoint)
                .icon(centerResource)
                .anchor(0.5f, 0.5f)
                .draggable(draggable));

        mRadiusMarkers = new ArrayList<>(radiusPoints.size());
        for (LatLng radiusPoint : radiusPoints) {
            final Marker radiusMarker = map.addMarker(new MarkerOptions()
                    .position(radiusPoint)
                    .icon(radiusResource)
                    .anchor(0.5f, 0.5f)
                    .draggable(draggable)
                    .visible(false));
            mRadiusMarkers.add(radiusMarker);
        }

        mPolygon = map.addPolygon(new PolygonOptions()
                .addAll(radiusPoints)
                .strokeWidth(strokeWidth)
                .strokeColor(strokeColor)
                .fillColor(fillColor));
    }

    public DraggablePolygon(GoogleMap map, List<LatLng> radiusPoints, int strokeColor,
                            int fillColor, float strokeWidth, BitmapDescriptor centerResource,
                            BitmapDescriptor radiusResource) {
        this(map, radiusPoints, strokeColor, fillColor, strokeWidth, centerResource, centerResource,
                radiusResource);
    }

    public DraggablePolygon(GoogleMap map, List<LatLng> radiusPoints, int strokeColor, int fillColor,
                            float strokeWidth, int centerResourceId, int radiusResourceId) {
        this(map, radiusPoints, strokeColor, fillColor, strokeWidth,
                BitmapDescriptorFactory.fromResource(centerResourceId),
                BitmapDescriptorFactory.fromResource(radiusResourceId));
    }

    public DraggablePolygon(GoogleMap map, List<LatLng> radiusPoints, int strokeColor, int fillColor,
                            float strokeWidth) {
        this(map, radiusPoints, strokeColor, fillColor, strokeWidth,
                BitmapDescriptorFactory.defaultMarker(),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
    }

    public DraggablePolygon(GoogleMap map, List<LatLng> radiusPoints) {
        this(map, radiusPoints, Color.BLUE, Color.TRANSPARENT, 1.f);
    }

    @Override
    public boolean onMarkerMoveStart(Marker marker) {
        if (marker.equals(mCenterMarker)) {
            if (!mRadiusMarkers.get(0).isVisible()) {
                onEditStart();
            }
        }
        return onMarkerMove(marker);
    }

    @Override
    public boolean onMarkerMove(Marker marker) {
        if (marker.equals(mCenterMarker)) {
            final double dlat = marker.getPosition().latitude - mCurrentCenterPoint.latitude;
            final double dlon = marker.getPosition().longitude - mCurrentCenterPoint.longitude;

            List<LatLng> radiusPoints = new ArrayList<>(mPolygon.getPoints().size());
            for (Marker radiusMarker : mRadiusMarkers) {
                final LatLng position = radiusMarker.getPosition();
                final LatLng newPosition = new LatLng(position.latitude + dlat,
                        position.longitude + dlon);

                radiusMarker.setPosition(newPosition);
                radiusPoints.add(newPosition);
            }
            mPolygon.setPoints(radiusPoints);

            mCurrentCenterPoint = mCenterMarker.getPosition();
            return true;
        } else {
            for (int i = 0; i < mRadiusMarkers.size(); i++) {
                final Marker radiusMarker = mRadiusMarkers.get(i);
                if (marker.equals(radiusMarker)) {
                    List<LatLng> radiusPoints = mPolygon.getPoints();

                    radiusPoints.set(i, marker.getPosition());
                    mPolygon.setPoints(radiusPoints);

                    LatLngBounds bounds = GeoUtils.getBounds(radiusPoints);
                    mCenterMarker.setPosition(bounds.getCenter());

                    mCurrentCenterPoint = mCenterMarker.getPosition();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onEditStart() {
        mCenterMarker.setIcon(mCenterEditingResource);

        for (Marker radiusMarker : mRadiusMarkers) {
            radiusMarker.setVisible(true);
        }
    }

    @Override
    public void onEditEnd() {
        mCenterMarker.setIcon(mCenterResource);

        for (Marker radiusMarker : mRadiusMarkers) {
            radiusMarker.setVisible(false);
        }

        mCenterPoint = mCenterMarker.getPosition();
//        mRadiusPoints = GeoUtils.getPointsForMarkers(mRadiusMarkers);
        mRadiusPoints = mPolygon.getPoints();
    }

    @Override
    public void onEditCancel() {
        mCenterMarker.setIcon(mCenterResource);

        for (Marker radiusMarker : mRadiusMarkers) {
            radiusMarker.setVisible(false);
        }

        mCenterMarker.setPosition(mCenterPoint);
        for (int i = 0; i < mRadiusMarkers.size(); i++) {
            Marker marker = mRadiusMarkers.get(i);
            marker.setPosition(mRadiusPoints.get(i));
        }

        mPolygon.setPoints(mRadiusPoints);
    }

    @Override
    public void setVisible(boolean visible) {
        mCenterMarker.setVisible(visible);
        mPolygon.setVisible(visible);

        if (!visible) {
            for (Marker radiusMarker : mRadiusMarkers) {
                radiusMarker.setVisible(visible);
            }
        }
    }

    @Override
    public boolean isVisible() {
        return mCenterMarker.isVisible();
    }

    @Override
    public void setDraggable(boolean draggable) {
        mCenterMarker.setDraggable(draggable);

        for (Marker marker : mRadiusMarkers) {
            marker.setDraggable(draggable);
        }
    }

    @Override
    public boolean isDraggable() {
        return mCenterMarker.isDraggable();
    }

    @Override
    public void setCenter(LatLng position) {
        mCenterMarker.setPosition(position);
        mCurrentCenterPoint = position;
    }

    @Override
    public LatLng getCenter() {
        return mCenterMarker.getPosition();
    }

    @Override
    public void setCenterAnchor(float x, float y) {
        mCenterMarker.setAnchor(x, y);
    }

    @Override
    public void setTitle(String title) {
        mCenterMarker.setTitle(title);
    }

    @Override
    public String getTitle() {
        return mCenterMarker.getTitle();
    }

    @Override
    public void setId(long id) {
        mId = id;
    }

    @Override
    public long getId() {
        return mId;
    }

    @Override
    public void remove() {
        mCenterMarker.remove();
        mPolygon.remove();

        for (Marker marker : mRadiusMarkers) {
            marker.remove();
        }
        mRadiusMarkers.clear();

        mCenterResource = null;
        mCenterEditingResource = null;
    }

    @Override
    public void onStyleChange(int strokeColor, int fillColor, float strokeWidth) {
        mPolygon.setStrokeWidth(strokeWidth);
        mPolygon.setFillColor(fillColor);
        mPolygon.setStrokeColor(strokeColor);
    }

    @Override
    public boolean isEditing() {
        return mRadiusMarkers.get(0).isVisible();
    }

    @Override
    public void setClickable(boolean clickable) {
        mPolygon.setClickable(clickable);
    }

    @Override
    public boolean isClickable() {
        return mPolygon.isClickable();
    }

    @Override
    public double getBiggestDistanceFromCenter() {
//        return GeoUtils.getBiggestDistanceFromCenterForMarkers(mCenterMarker.getPosition(),
//                mRadiusMarkers);
        return GeoUtils.getBiggestDistanceFromCenterForMarkers(mRadiusMarkers);
    }

    @Override
    public LatLngBounds getBounds() {
        return GeoUtils.getBoundsForMarkers(mRadiusMarkers);
    }

    @Override
    public List<LatLng> getShape() {
        return GeoUtils.getPointsForMarkers(mRadiusMarkers);
    }
}
