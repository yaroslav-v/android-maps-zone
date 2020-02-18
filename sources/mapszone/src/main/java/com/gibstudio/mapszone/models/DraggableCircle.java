package com.gibstudio.mapszone.models;

import com.gibstudio.mapszone.GeoUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class DraggableCircle implements DraggableShape {

    private final Marker mCenterMarker;
    private final Marker mRadiusMarker;
    private final Circle mCircle;

    private BitmapDescriptor mCenterResource;
    private BitmapDescriptor mCenterEditingResource;

    private long mId = GeoUtils.INCORRECT_VALUE;

    private LatLng mCenterPoint;
    private double mRadius;

    public DraggableCircle(GoogleMap map, LatLng centerPoint, double radius, int strokeColor,
                           int fillColor, float strokeWidth, BitmapDescriptor centerResource,
                           BitmapDescriptor centerEditingResource, BitmapDescriptor radiusResource) {
        final boolean draggable = true;

        mCenterPoint = centerPoint;
        mRadius = radius;

        mCenterResource = centerResource;
        mCenterEditingResource = centerEditingResource;

        mCenterMarker = map.addMarker(new MarkerOptions()
                .position(centerPoint)
                .icon(centerResource)
                .anchor(0.5f, 0.5f)
                .draggable(draggable));

        mRadiusMarker = map.addMarker(new MarkerOptions()
                .position(GeoUtils.toRadiusLatLng(centerPoint, radius))
                .icon(radiusResource)
                .anchor(0.5f, 0.5f)
                .draggable(draggable)
                .visible(false));

        mCircle = map.addCircle(new CircleOptions()
                .center(centerPoint)
                .radius(radius)
                .strokeWidth(strokeWidth)
                .strokeColor(strokeColor)
                .fillColor(fillColor));
    }

    public DraggableCircle(GoogleMap map, LatLng centerPoint, double radius, int strokeColor,
                           int fillColor, float strokeWidth) {
        this(map, centerPoint, radius, strokeColor, fillColor, strokeWidth,
                BitmapDescriptorFactory.defaultMarker(),
                BitmapDescriptorFactory.defaultMarker(),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
    }

    public DraggableCircle(GoogleMap map, LatLng centerPoint, LatLng radiusPoint, int strokeColor,
                           int fillColor, float strokeWidth, BitmapDescriptor centerResource,
                           BitmapDescriptor centerEditingResource, BitmapDescriptor radiusResource) {
        this(map, centerPoint, GeoUtils.toRadiusMeters(centerPoint, radiusPoint), strokeColor,
                fillColor, strokeWidth, centerResource, centerEditingResource, radiusResource);
    }

    public DraggableCircle(GoogleMap map, LatLng centerPoint, LatLng radiusPoint, int strokeColor,
                           int fillColor, float strokeWidth) {
        this(map, centerPoint, radiusPoint, strokeColor, fillColor, strokeWidth,
                BitmapDescriptorFactory.defaultMarker(),
                BitmapDescriptorFactory.defaultMarker(),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
    }

    @Override
    public boolean onMarkerMoveStart(Marker marker) {
        if (marker.equals(mCenterMarker)) {
            if (!mRadiusMarker.isVisible()) {
                onEditStart();
            }
        }
        return onMarkerMove(marker);
    }

    @Override
    public boolean onMarkerMove(Marker marker) {
        if (marker.equals(mCenterMarker)) {
            mCircle.setCenter(marker.getPosition());
            mRadiusMarker.setPosition(GeoUtils.toRadiusLatLng(marker.getPosition(), mCircle.getRadius()));
            return true;
        } else if (marker.equals(mRadiusMarker)) {
            double radius = GeoUtils.toRadiusMeters(mCenterMarker.getPosition(), mRadiusMarker.getPosition());
            mCircle.setRadius(radius);
            return true;
        }
        return false;
    }

    @Override
    public void onEditStart() {
        mCenterMarker.setIcon(mCenterEditingResource);

        mRadiusMarker.setVisible(true);
    }

    @Override
    public void onEditEnd() {
        mCenterMarker.setIcon(mCenterResource);

        mRadiusMarker.setVisible(false);

        mCenterPoint = mCenterMarker.getPosition();
        mRadius = mCircle.getRadius();
    }

    @Override
    public void onEditCancel() {
        mCenterMarker.setIcon(mCenterResource);

        mRadiusMarker.setVisible(false);

        mCenterMarker.setPosition(mCenterPoint);
        mRadiusMarker.setPosition(GeoUtils.toRadiusLatLng(mCenterPoint, mRadius));

        mCircle.setCenter(mCenterPoint);
        mCircle.setRadius(mRadius);
    }

    @Override
    public void setZIndex(float index) {
        mCenterMarker.setZIndex(index);
        mRadiusMarker.setZIndex(index);
        mCircle.setZIndex(index);
    }

    @Override
    public float getZIndex() {
        return mCircle.getZIndex();
    }

    @Override
    public void setVisible(boolean visible) {
        mCenterMarker.setVisible(visible);
        mCircle.setVisible(visible);

        if (!visible) {
            mRadiusMarker.setVisible(visible);
        }
    }

    @Override
    public boolean isVisible() {
        return mCenterMarker.isVisible();
    }

    @Override
    public void setDraggable(boolean draggable) {
        mCenterMarker.setDraggable(draggable);
        mRadiusMarker.setDraggable(draggable);
    }

    @Override
    public boolean isDraggable() {
        return mCenterMarker.isDraggable();
    }

    @Override
    public void setCenter(LatLng position) {
        mCenterMarker.setPosition(position);
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
        mRadiusMarker.remove();
        mCircle.remove();

        mCenterResource = null;
        mCenterEditingResource = null;

        mCenterPoint = null;
        mRadius = 0;
    }

    @Override
    public void onStyleChange(int strokeColor, int fillColor, float strokeWidth) {
        mCircle.setStrokeWidth(strokeWidth);
        mCircle.setFillColor(fillColor);
        mCircle.setStrokeColor(strokeColor);
    }

    @Override
    public boolean isEditing() {
        return mRadiusMarker.isVisible();
    }

    @Override
    public void setClickable(boolean clickable) {
        mCircle.setClickable(clickable);
    }

    @Override
    public boolean isClickable() {
        return mCircle.isClickable();
    }

    @Override
    public double getBiggestDistanceFromCenter() {
        return mCircle.getRadius();
    }

    @Override
    public LatLngBounds getBounds() {
        return GeoUtils.getBounds(GeoUtils.convertCircleToPolygon(mCenterMarker.getPosition(),
                mCircle.getRadius()));
    }

    @Override
    public List<LatLng> getShape() {
        return GeoUtils.convertCircleToPolygon(mCenterMarker.getPosition(), mCircle.getRadius());
    }
}
