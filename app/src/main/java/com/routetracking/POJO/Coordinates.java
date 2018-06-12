package com.routetracking.POJO;

import com.orm.SugarRecord;

import com.orm.dsl.Table;

@Table
public class Coordinates extends SugarRecord {
    private long route_id;
    private double latitude,longitude;


    public Coordinates() {
    }

    public Coordinates(long route_id,  double latitude, double longitude) {
        this.route_id = route_id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getRoute_id() {
        return route_id;
    }

    public void setRoute_id(long route_id) {
        this.route_id = route_id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


}
