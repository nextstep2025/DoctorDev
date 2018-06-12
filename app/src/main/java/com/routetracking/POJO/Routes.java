package com.routetracking.POJO;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

@Table
public class Routes extends SugarRecord {


    private String name;
    private String startTrackTime,endTrackTime,timeDiff;
    public Routes() {
    }

    public Routes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTrackTime() {
        return startTrackTime;
    }

    public void setStartTrackTime(String startTrackTime) {
        this.startTrackTime = startTrackTime;
    }

    public String getEndTrackTime() {
        return endTrackTime;
    }

    public void setEndTrackTime(String endTrackTime) {
        this.endTrackTime = endTrackTime;
    }

    public String getTimeDiff() {
        return timeDiff;
    }

    public void setTimeDiff(String timeDiff) {
        this.timeDiff = timeDiff;
    }
}
