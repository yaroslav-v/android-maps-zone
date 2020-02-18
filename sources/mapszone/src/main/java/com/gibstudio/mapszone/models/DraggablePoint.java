package com.gibstudio.mapszone.models;

import com.gibstudio.mapszone.GeoUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class DraggablePoint implements Draggable {

    private final Marker mCenterMarker;

    private BitmapDescriptor mCenterResource;
    private BitmapDescriptor mCenterEditingResource;

    private long mId = GeoUtils.INCORRECT_VALUE;

    private LatLng mCenterPoint;

    public DraggablePoint(GoogleMap map, LatLng centerPoint, BitmapDescriptor centerResource,
                          BitmapDescriptor centerEditingResource) {
        final boolean draggable = true;

        mCenterPoint = centerPoint;

        mCenterResource = centerResource;
        mCenterEditingResource = centerEditingResource;

        mCenterMarker = map.addMarker(new MarkerOptions()
                .position(centerPoint)
                .icon(centerResource)
                .anchor(0.5f, 0.5f)
                .draggable(draggable));
    }

    public DraggablePoint(GoogleMap map, LatLng centerPoint) {
        this(map, centerPoint, BitmapDescriptorFactory.defaultMarker(),
                BitmapDescriptorFactory.defaultMarker());
    }

    @Override
    public boolean onMarkerMoveStart(Marker marker) {
        if (marker.equals(mCenterMarker)) {
            onEditStart();
        }
        return onMarkerMove(marker);
    }

    @Override
    public boolean onMarkerMove(Marker marker) {
        if (marker.equals(mCenterMarker)) {
            return true;
        }
        return false;
    }

    @Override
    public void onEditStart() {
        mCenterMarker.setIcon(mCenterEditingResource);
    }

    @Override
    public void onEditEnd() {
        mCenterMarker.setIcon(mCenterResource);

        mCenterPoint = mCenterMarker.getPosition();
    }

    @Override
    public void onEditCancel() {
        mCenterMarker.setIcon(mCenterResource);

        mCenterMarker.setPosition(mCenterPoint);
    }

    @Override
    public void setZIndex(float index) {
        mCenterMarker.setZIndex(index);
    }

    @Override
    public float getZIndex() {
        return mCenterMarker.getZIndex();
    }

    @Override
    public void setVisible(boolean visible) {
        mCenterMarker.setVisible(visible);
    }

    @Override
    public boolean isVisible() {
        return mCenterMarker.isVisible();
    }

    @Override
    public void setDraggable(boolean draggable) {
        mCenterMarker.setDraggable(draggable);
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

        mCenterResource = null;
        mCenterEditingResource = null;
    }
}
