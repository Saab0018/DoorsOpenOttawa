package com.algonquincollege.saab0018.doorsopenottawa.model;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mattsaab and alve0024 on 2016-11-08.
 */

public class Building {

    // Instance Variables
    private int buildingId;
    private String name;
    private String address;
    private String image;
    private Bitmap bitmap;
    private String description;
    private List<String> openHours = new ArrayList<>();

    // Getters
    public String getAddress() {return address;}
    public String getImage() {return image;}
    public int getBuildingId() {return buildingId;}
    public String getName() {return name;}
    public Bitmap getBitmap(){return bitmap;}
    public String getDescription(){return description;}
    public List<String> getOpenHours() {return openHours;}

    // Setters
    public void setAddress(String address) {
        this.address = address + " Ottawa, Ontario";
    }
    public void setImage(String image) {
        this.image = image;
    }
    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setBitmap(Bitmap bitmap) {this.bitmap = bitmap;}
    public void setDescription(String description) {this.description = description;}

    public void addDate(String date) {this.openHours.add(date);}

}
