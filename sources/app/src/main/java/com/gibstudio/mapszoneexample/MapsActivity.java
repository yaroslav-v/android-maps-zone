package com.gibstudio.mapszoneexample;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;

import com.gibstudio.mapszone.models.Draggable;
import com.gibstudio.mapszone.models.DraggableCircle;
import com.gibstudio.mapszone.models.DraggableShape;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerDragListener, GoogleMap.OnMapLongClickListener{

    private GoogleMap mMap;
    private List<DraggableShape> mDraggableObjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);

        // add a marker in Sydney and move the camera
        LatLng latLng = new LatLng(55.7558, 37.6173);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

        final int[] colors = getRandomColor();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        DraggableShape draggable = new DraggableCircle(mMap, // GoogleMap instance
                latLng, // LatLng coordinate of center point
                (metrics.widthPixels * 100) / mMap.getCameraPosition().zoom, // double radius of circle
                colors[0], // int stroke Color
                colors[1], // int fill Color
                10); // float stroke width
        draggable.setTitle("Marker in Moskow");
        draggable.setZIndex(1.0f);
        mDraggableObjects.add(draggable);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        final int[] colors = getRandomColor();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        DraggableShape draggable = new DraggableCircle(mMap, // GoogleMap instance
                latLng, // LatLng coordinate of center point
                (metrics.widthPixels * 100) / mMap.getCameraPosition().zoom, // double radius of circle
                colors[0], // int stroke Color
                colors[1], // int fill Color
                10); // float stroke width
        draggable.setZIndex(0.0f);
        mDraggableObjects.add(draggable);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        onMarkerMoveStart(marker);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        onMarkerMove(marker);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        onMarkerMove(marker);
    }

    private void onMarkerMoveStart(Marker marker) {
        for (DraggableShape draggable : mDraggableObjects) {
            if (draggable.onMarkerMoveStart(marker)) {
                break;
            }
        }
    }

    private void onMarkerMove(Marker marker) {
        for (DraggableShape draggable : mDraggableObjects) {
            if (draggable.onMarkerMove(marker)) {
                break;
            }
        }
    }

    private int[] getRandomColor() {
        Random rnd = new Random();
        final int r = rnd.nextInt(256);
        final int g = rnd.nextInt(256);
        final int b = rnd.nextInt(256);

        final int[] colors = new int[2];
        colors[0] = Color.argb(255, r, g, b); // stroke color
        colors[1] = Color.argb(102, r, g, b); // fill color

        return colors;
    }
}
