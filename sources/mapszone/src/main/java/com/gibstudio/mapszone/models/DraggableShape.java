package com.gibstudio.mapszone.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

public interface DraggableShape extends Draggable {

    void onStyleChange(int strokeColor, int fillColor, float strokeWidth);

    boolean isEditing();

    void setClickable(boolean clickable);

    boolean isClickable();

    double getBiggestDistanceFromCenter();

    LatLngBounds getBounds();

    List<LatLng> getShape();
}
