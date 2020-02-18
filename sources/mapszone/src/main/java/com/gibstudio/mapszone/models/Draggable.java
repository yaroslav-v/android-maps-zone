package com.gibstudio.mapszone.models;

import android.os.RemoteException;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.RuntimeRemoteException;

public interface Draggable {

    boolean onMarkerMoveStart(Marker marker);

    boolean onMarkerMove(Marker marker);

    void onEditStart();

    void onEditEnd();

    void onEditCancel();

    void setZIndex(float index);

    float getZIndex();

    void setVisible(boolean visible);

    boolean isVisible();

    void setDraggable(boolean draggable);

    boolean isDraggable();

    void setCenter(LatLng position);

    LatLng getCenter();

    void setCenterAnchor(float x, float y);

    void setTitle(String title);

    String getTitle();

    void setId(long id);

    long getId();

    void remove();
}
