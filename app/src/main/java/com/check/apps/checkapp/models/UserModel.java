package com.check.apps.checkapp.models;

import java.io.Serializable;
import java.util.List;

public class UserModel implements Serializable {

    private String id;
    private String nurse_id;
    private String nurse_name;
    private String doctor_id;
    private String doctor_name;
    private int user_type;
    private String name;
    private String email;
    private String phone_code;
    private String phone;
    private int gender;
    private String blood_type;
    private String age;
    private String birth_date;
    private String city;
    private String password;
    private boolean fingerprint;
    private String department;
    private boolean isAvailable;
    private List<AppointmentModel> appointmentModelList;

    public UserModel() {
    }


    public UserModel(String id, String nurse_id,String nurse_name,String doctor_id,String doctor_name, int user_type, String name, String email, String phone_code, String phone, int gender, String blood_type, String age, String birth_date, String city, String password, boolean fingerprint, String department, boolean isAvailable, List<AppointmentModel> appointmentModelList) {
        this.id = id;
        this.nurse_id = nurse_id;
        this.nurse_name = nurse_name;
        this.doctor_id = doctor_id;
        this.doctor_name = doctor_name;
        this.user_type = user_type;
        this.name = name;
        this.email = email;
        this.phone_code = phone_code;
        this.phone = phone;
        this.gender = gender;
        this.blood_type = blood_type;
        this.age = age;
        this.birth_date = birth_date;
        this.city = city;
        this.password = password;
        this.fingerprint = fingerprint;
        this.department = department;
        this.isAvailable = isAvailable;
        this.appointmentModelList = appointmentModelList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNurse_id() {
        return nurse_id;
    }

    public void setNurse_id(String nurse_id) {
        this.nurse_id = nurse_id;
    }

    public String getNurse_name() {
        return nurse_name;
    }

    public void setNurse_name(String nurse_name) {
        this.nurse_name = nurse_name;
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

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_code() {
        return phone_code;
    }

    public void setPhone_code(String phone_code) {
        this.phone_code = phone_code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getBlood_type() {
        return blood_type;
    }

    public void setBlood_type(String blood_type) {
        this.blood_type = blood_type;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(boolean fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<AppointmentModel> getAppointmentModelList() {
        return appointmentModelList;
    }

    public void setAppointmentModelList(List<AppointmentModel> appointmentModelList) {
        this.appointmentModelList = appointmentModelList;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
