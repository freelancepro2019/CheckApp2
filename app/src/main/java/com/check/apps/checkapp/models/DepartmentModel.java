package com.check.apps.checkapp.models;

import java.io.Serializable;

public class DepartmentModel implements Serializable {

    private int id;
    private String department_name;
    private int image_resource;

    public DepartmentModel() {
    }

    public DepartmentModel(int id, String department_name, int image_resource) {
        this.id = id;
        this.department_name = department_name;
        this.image_resource = image_resource;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDepartment_name() {
        return department_name;
    }

    public void setDepartment_name(String department_name) {
        this.department_name = department_name;
    }

    public int getImage_resource() {
        return image_resource;
    }

    public void setImage_resource(int image_resource) {
        this.image_resource = image_resource;
    }
}
