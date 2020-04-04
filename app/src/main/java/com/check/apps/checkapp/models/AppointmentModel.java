package com.check.apps.checkapp.models;

import java.io.Serializable;

public class AppointmentModel implements Serializable {

    private String day;
    private String from_time;
    private String to_time;

    public AppointmentModel() {
    }

    public AppointmentModel(String day, String from_time, String to_time) {
        this.day = day;
        this.from_time = from_time;
        this.to_time = to_time;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getFrom_time() {
        return from_time;
    }

    public void setFrom_time(String from_time) {
        this.from_time = from_time;
    }

    public String getTo_time() {
        return to_time;
    }

    public void setTo_time(String to_time) {
        this.to_time = to_time;
    }
}
