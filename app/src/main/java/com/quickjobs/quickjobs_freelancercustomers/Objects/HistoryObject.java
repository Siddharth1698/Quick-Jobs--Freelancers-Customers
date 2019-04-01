package com.quickjobs.quickjobs_freelancercustomers.Objects;



public class HistoryObject {
    private String rideId;
    private String time,jobd;


    public HistoryObject(String rideId, String time, String jobd){
        this.rideId = rideId;
        this.time = time;
        this.jobd = jobd;
    }

    public String getJobd() {
        return jobd;
    }

    public void setJobd(String jobd) {
        this.jobd = jobd;
    }

    public String getRideId(){return rideId;}
    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getTime(){return time;}
    public void setTime(String time) {
        this.time = time;
    }
}