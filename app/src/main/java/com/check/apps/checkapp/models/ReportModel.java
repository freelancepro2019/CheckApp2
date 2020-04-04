package com.check.apps.checkapp.models;

import java.io.Serializable;

public class ReportModel implements Serializable {

    private String id;
    private String patient_id;
    private String patient_name;
    private String doctor_id;
    private String doctor_name;
    private String content;

    public ReportModel() {
    }

    public ReportModel(String id, String patient_id, String patient_name, String doctor_id, String doctor_name, String content) {
        this.id = id;
        this.patient_id = patient_id;
        this.patient_name = patient_name;
        this.doctor_id = doctor_id;
        this.doctor_name = doctor_name;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
