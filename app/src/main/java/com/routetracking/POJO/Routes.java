package com.routetracking.POJO;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

@Table
public class Routes extends SugarRecord {


    private String name;

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
}
