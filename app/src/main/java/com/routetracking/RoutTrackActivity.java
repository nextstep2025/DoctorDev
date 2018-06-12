package com.routetracking;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.routetracking.POJO.Coordinates;
import com.routetracking.POJO.Routes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.os.Build.VERSION_CODES.M;

public class RoutTrackActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private Button startTrack, stopTrack;
    private String TAG = "ROUTE TRACKING ";
    private long routeId;
    private ArrayList<LatLng> points;
    Polyline line;
    private boolean isTracking = false;
    private Location coordPrevious, coordNext;
    private float distanceBetweenCoord = 0;
    /**
     * Code used in requesting runtime permissions.
     */
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    /**
     * Constant used in the location settings dialog.
     */
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;


    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Provides access to the Location Settings API.
     */
    private SettingsClient mSettingsClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Callback for Location events.
     */
    private LocationCallback mLocationCallback;

    /**
     * Represents a geographical location.
     */
    private Location mCurrentLocation;
    //request location only ones
    private boolean mRequestingLocationUpdates;
    private int polyLineColor;
    private float distance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rout_track);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        points = new ArrayList<LatLng>();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(RoutTrackActivity.this);
        polyLineColor = prefs.getInt("polylinecolor", 0);

        routeId = getIntent().getLongExtra("route_id", 0);

        System.out.println("routeId = " + routeId);

        startTrack = findViewById(R.id.start_track);
        stopTrack = findViewById(R.id.stop_track);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();

        buttonClicks();
    }


    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        setRunTimePermission();

    }


    private void setRunTimePermission() {
        //   if (new ConnectionDetector(GetCoordinate.this).isConnectingToInternet()) {
        if (Build.VERSION.SDK_INT >= M) {
            if (checkPermission()) {
                Log.d(TAG, "ALREADY GIVEN PERMISSION ");
                startLocationUpdates();

            } else {
                //set location permission
                Log.d(TAG, "SHOW PERMISSION DIALOG");
                ActivityCompat.requestPermissions(RoutTrackActivity.this
                        , new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);

            }

        } else {
            Log.d(TAG, "NO RUN TIME PERMISSION FOR < 6  ");
            startLocationUpdates();
        }
        //}

//        else {
//            android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
//            alertDialogBuilder.setTitle(R.string.oop).setMessage(R.string.sorry_at_the_moment_there_seems_to_be_a_problem_with_the_network).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            }).show();
//
//
//        }

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this
                , android.Manifest.permission.ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;

    }


    //calll for run time permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "CLICKED ALLOW PERMISSION GRANTED ");
                    startLocationUpdates();

                } else {
                    noPermisAlert();
                    Log.d(TAG, "CLICKED  DENIED & DONT ALLOW ");

                }
                break;

            default:
                noPermisAlert();
        }
    }


    private void noPermisAlert() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(RoutTrackActivity.this);
        alertDialogBuilder.setTitle(R.string.unable_to_access_the_Location)
                .setMessage(R.string.to_enable_access_go_to_Settings_Privacy_location_services_and_turn_on_location_access_for_this_app)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                }).show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        // mHandler.postDelayed(mExpiredRunnable, 30000);
                        // showProg();

                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        //failedTogetCoordinates();
                        break;

                    default:

                        // failedTogetCoordinates();
                }
                break;

            default:
                //failedTogetCoordinates();
        }
    }


    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    /**
     * Creates a callback for receiving location events.
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                updateLocationData();
            }
        };
    }


    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }


    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //noinspection MissingPermission
                        if (ActivityCompat.checkSelfPermission(RoutTrackActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        // System.out.println("mCurrentLocation.getLatitude() = " + mCurrentLocation.getLatitude());
                        //  System.out.println("mCurrentLocation.getLongitude() = " + mCurrentLocation.getLongitude());

                        // showProg();
                        getLocationCoordinates();

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(RoutTrackActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");

                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                mRequestingLocationUpdates = false;
                                break;
                            //found issue while testing on VIVO phone it was coming 10 status code
                            //known issue found on  SO-refer -->https://stackoverflow.com/questions/50404364/apiexception-which-checking-for-location-settings-in-fusedlocationapi
                            default:
                                //  failedTogetCoordinates();
                                mRequestingLocationUpdates = false;

                        }
                        //
                        // getLocationCoordinates();
                    }
                });
    }

    private void getLocationCoordinates() {
        updateLocationData();

    }


    private void updateLocationData() {
        if (mCurrentLocation != null) {

            double lat = mCurrentLocation.getLatitude();
            double longi = mCurrentLocation.getLongitude();
//            System.out.println("GOT LAT LONG AFTER LOCATION REQ. WHEN GPS WAS OFF on START" + lat + "Long:=" + longi);

            //   mRequestingLocationUpdates = false;


            System.out.println("mCurrentLocation.getLatitude() = " + mCurrentLocation.getLatitude());
            System.out.println("mCurrentLocation.getLongitude() = " + mCurrentLocation.getLongitude());
            mMap.clear();


            coordNext = new Location("");
            coordNext.setLatitude(mCurrentLocation.getLatitude());
            coordNext.setLongitude(mCurrentLocation.getLongitude());


            if (coordPrevious != null && coordNext != null) {

                distanceBetweenCoord = coordPrevious.distanceTo(coordNext);
                distance = distance + distanceBetweenCoord;
                coordPrevious = coordNext;
            }

            LatLng loca = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
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
            mMap.setMyLocationEnabled(true);
            //  mMap.addMarker(new MarkerOptions().position(loca));
            //  mMap.moveCamera(CameraUpdateFactory.newLatLng(loca));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loca, 15));

//            if (isTracking) {
//
//
//                coordPrevious = new Location("");
//                coordPrevious.setLatitude(mCurrentLocation.getLatitude());
//                coordPrevious.setLongitude(mCurrentLocation.getLatitude());
//
//                coordNext = null;
//                distanceBetweenCoord = 0;
//
//
//                Coordinates coordinates = new Coordinates(routeId, loca.latitude, loca.longitude);
//                coordinates.save();
//                points.add(loca);
//
//                redrawLine();
//
//            }


        }
    }

    private void redrawLine() {

        if (isTracking) {

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
            mMap.setMyLocationEnabled(true);
            PolylineOptions options = new PolylineOptions().width(15).color(polyLineColor).geodesic(true);
            for (int i = 0; i < points.size(); i++) {
                LatLng point = points.get(i);
                options.add(point);
            }
            // addMarker(); //add Marker in current position
            line = mMap.addPolyline(options); //add Polyline

        }
    }


    private void buttonClicks() {

        startTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isTracking = true;
                Constant.STARTTRACKTIME = Constant.getCurrentDatenTime();
                //  System.out.println("Date"+Constant.getCurrentDatenTime());


                if (isTracking) {


                    coordPrevious = new Location("");
                    coordPrevious.setLatitude(mCurrentLocation.getLatitude());
                    coordPrevious.setLongitude(mCurrentLocation.getLongitude());

                    coordNext = null;
                    distanceBetweenCoord = 0;


                    LatLng loca = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    Coordinates coordinates = new Coordinates(routeId, loca.latitude, loca.longitude);
                    coordinates.save();
                    points.add(loca);

                    redrawLine();

                }


            }
        });


        stopTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isTracking = false;


                Constant.ENDTRACKTIME = Constant.getCurrentDatenTime();

                if (mFusedLocationClient != null) {
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                }
                stopLocationUpdates();


                String dateStart = Constant.STARTTRACKTIME;
                String dateStop = Constant.ENDTRACKTIME;

                //HH converts hour in 24 hours format (0-23), day calculation
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

                Date d1 = null;
                Date d2 = null;


                long diffSeconds = 0;
                try {
                    d1 = format.parse(dateStart);
                    d2 = format.parse(dateStop);

                    //in milliseconds
                    long diff = d2.getTime() - d1.getTime();

                    diffSeconds = diff / 1000 % 60;
                    long diffMinutes = diff / (60 * 1000) % 60;
                    long diffHours = diff / (60 * 60 * 1000) % 24;
                    long diffDays = diff / (24 * 60 * 60 * 1000);

                    System.out.print(diffDays + " days, ");
                    System.out.print(diffHours + " hours, ");
                    System.out.print(diffMinutes + " minutes, ");
                    System.out.print(diffSeconds + " seconds.");

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Routes finalRouteTimings = Routes.findById(Routes.class, routeId);
                finalRouteTimings.setStartTrackTime(Constant.STARTTRACKTIME);  // modify the values
                finalRouteTimings.setEndTrackTime(Constant.ENDTRACKTIME);
                finalRouteTimings.setTimeDiff(String.valueOf(diffSeconds));
                finalRouteTimings.save(); // updates the previous entry with new values.

            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
        stopLocationUpdates();


    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {

            return;
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;

                    }
                });
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


    }

    @Override
    public void onLocationChanged(Location location) {

        // System.out.println("lat"+location.getLatitude());
        // System.out.println("long"+location.getLongitude());

        Toast.makeText(this, "lat" + location.getLatitude() + "long" + location.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
