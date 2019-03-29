package com.quickjobs.quickjobs_freelancercustomers.Objects;



public class CustomerHistoryLJObject {
    private String title,desc,location,cat,status;
    private String time;


    public CustomerHistoryLJObject(String title, String desc, String location, String cat, String time, String status) {
        this.title = title;
        this.desc = desc;
        this.location = location;
        this.cat = cat;
        this.time = time;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}