package com.kallinikos.tech.sweetdealsfire.dbmodels;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.List;

/**
 * Created by kallinikos on 01/02/17.
 */

@IgnoreExtraProperties
public class Ad {
    public String title;
    public String description;
    public int price;
    public HashMap<String,String> imgNames;
    public String user;
    public String category;
    public String subcategory;
    public HashMap<String,Object> timestampCreated;


    public Ad(){
        //Default Constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Ad(String title, String description, int price, HashMap<String,String> imgNames, String user, String category, String subcategory) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.imgNames = imgNames;
        this.user = user;
        this.category = category;
        this.subcategory = subcategory;
        HashMap<String,Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public String getUser() {
        return user;
    }

    public String getCategory() {
        return category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public HashMap<String, String> getImgNames() {
        return imgNames;
    }

    @Exclude
    public long getTimestampCreatedLong(){
        return (long)timestampCreated.get("timestamp");
    }
}
