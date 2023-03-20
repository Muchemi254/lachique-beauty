package com.example.LachiqueBeauty.Users;

public class Booking {

    String id;
    String date;
    String time;
    String nailist;
    String service;


       public Booking(String id, String date, String time, String nailist, String service){
        this.id =id;
        this.date = date;
        this.time = time;
        this.nailist = nailist;
        this.service = service;
    }



    public String getId() {
        return id;
    }
    public void setId(String id){
        this.id = id;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date){
        this.date = date;
    }

    public String getTime() {

        return time;
    }
    public void setTime(String time){
        this.time = time;
    }


    public String getNailist() {
        return nailist;
    }
    public void setNailist(String nailist){
        this.nailist = nailist;
    }

    public String getService() {
        return service;
    }
    public void setService(String service){
        this.service = service;
    }




}
