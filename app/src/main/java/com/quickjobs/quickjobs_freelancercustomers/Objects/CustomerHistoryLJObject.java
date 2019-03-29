package com.quickjobs.quickjobs_freelancercustomers.Objects;



public class CustomerHistoryLJObject {
    private String title,desc,location,cat,status,ridekey;
    private String time;



    public CustomerHistoryLJObject(String title, String desc, String location, String cat, String time, String status,String ridekey) {
        this.title = title;
        this.desc = desc;
        this.location = location;
        this.cat = cat;
        this.time = time;
        this.status = status;
        this.ridekey = ridekey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRidekey() {
        return ridekey;
    }

    public void setRidekey(String ridekey) {
        this.ridekey = ridekey;
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