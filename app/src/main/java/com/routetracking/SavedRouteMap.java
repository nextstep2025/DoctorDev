package com.routetracking;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.routetracking.POJO.Coordinates;

import java.util.ArrayList;
import java.util.List;

public class SavedRouteMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private long routeId;
    private ArrayList<LatLng> savedMapPoints;
    Polyline savedLine;
    private LatLngBounds.Builder builder;
    private SupportMapFragment mapFragment;
    private int polyLineColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_saved_route_map);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SavedRouteMap.this);
        polyLineColor = prefs.getInt("polylinecolor", 0);

        savedMapPoints = new ArrayList<LatLng>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
         mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        routeId = getIntent().getLongExtra("route_id", 0);
        builder = new LatLngBounds.Builder();
        List<Coordinates> lat = Coordinates.findWithQuery(Coordinates.class
                                 , "Select ID,LATITUDE,LONGITUDE from COORDINATES where ROUTEID = ? ORDER BY ID"
                                 , Long.toString(routeId));

        System.out.println(lat.get(0));

        for (int i = 0; i < lat.size(); i++) {
            LatLng loca = new LatLng(lat.get(i).getLatitude(), lat.get(i).getLongitude());

            savedMapPoints.add(loca);
            builder.include(loca);


        }

        System.out.println("routeId = " + routeId);
        System.out.println("savedpoly"+savedMapPoints.get(0).latitude);
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

        mMap.clear();  //clears all Markers and Polylines
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LatLngBounds bounds = builder.build();
        DisplayMetrics mapFragDisplayMetrics = mapFragment.getResources().getDisplayMetrics();
        int width = mapFragDisplayMetrics.widthPixels;
        int height = mapFragDisplayMetrics.heightPixels;
        int padding = (int) (width * 0.08); // offset from edges of the map 12% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.setMyLocationEnabled(true);
        PolylineOptions options = new PolylineOptions().width(15).color(polyLineColor).geodesic(true);

     //   for (int i = 0; i < savedMapPoints.size(); i++) {
           // LatLng point = savedMapPoints.get(i);
            options.addAll(savedMapPoints);
      //  }
        savedLine = mMap.addPolyline(options); //add Polyline

        mMap.moveCamera(cu);
        mMap.animateCamera(cu);
    }
}
