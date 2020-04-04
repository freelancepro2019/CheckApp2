package com.check.apps.checkapp.models;

import java.io.Serializable;

public class AppointmentReserveModel implements Serializable {

    private String id;
    private String day;
    private String from_time;
    private String to_time;
    private String user_id;
    private String user_name;
    private String doctor_id;
    private String doctor_name;
    private String problem;
    private String create_at;
    private int status;


    public AppointmentReserveModel() {

    }

    public AppointmentReserveModel(String id, String create_at, String day, String from_time, String to_time, String user_id, String user_name, String doctor_id, String doctor_name, String problem,int status) {
        this.id = id;
        this.create_at = create_at;
        this.day = day;
        this.from_time = from_time;
        this.to_time = to_time;
        this.user_id = user_id;
        this.user_name = user_name;
        this.doctor_id = doctor_id;
        this.doctor_name = doctor_name;
        this.problem = problem;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
