package com.check.apps.checkapp.models;

import java.io.Serializable;

public class TechnicianReportModel implements Serializable {

    private String id;
    private String technician_id;
    private String technician_name;
    private String doctor_id;
    private String doctor_name;
    private String patient_id;
    private String patient_name;
    private String report;
    private String create_at;

    public TechnicianReportModel() {
    }

    public TechnicianReportModel(String id, String technician_id, String technician_name, String doctor_id, String doctor_name, String patient_id, String patient_name, String report, String create_at) {
        this.id = id;
        this.technician_id = technician_id;
        this.technician_name = technician_name;
        this.doctor_id = doctor_id;
        this.doctor_name = doctor_name;
        this.patient_id = patient_id;
        this.patient_name = patient_name;
        this.report = report;
        this.create_at = create_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTechnician_id() {
        return technician_id;
    }

    public void setTechnician_id(String technician_id) {
        this.technician_id = technician_id;
    }

    public String getTechnician_name() {
        return technician_name;
    }

    public void setTechnician_name(String technician_name) {
        this.technician_name = technician_name;
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

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }
}
