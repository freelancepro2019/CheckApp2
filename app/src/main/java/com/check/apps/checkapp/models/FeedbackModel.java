package com.check.apps.checkapp.models;

import java.io.Serializable;

public class FeedbackModel implements Serializable {

    private String id;
    private String from_name;
    private String from_id;
    private String content;
    private String time;

    public FeedbackModel() {
    }

    public FeedbackModel(String id, String from_name, String from_id, String content, String time) {
        this.id = id;
        this.from_name = from_name;
        this.from_id = from_id;
        this.content = content;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
