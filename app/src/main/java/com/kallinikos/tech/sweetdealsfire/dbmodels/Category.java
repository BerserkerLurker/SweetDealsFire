package com.kallinikos.tech.sweetdealsfire.dbmodels;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

/**
 * Created by kallinikos on 04/02/17.
 */

@IgnoreExtraProperties
public class Category {
    public HashMap<String, Integer> ads;
    public HashMap<String, Boolean> subCategories;

    public Category(){

    }

    public Category(HashMap<String, Integer> ads, HashMap<String, Boolean> subCategories){
        this.ads = ads;
        this.subCategories = subCategories;
    }

    public HashMap<String, Integer> getAds() {
        return ads;
    }

    public HashMap<String, Boolean> getSubCategories() {
        return subCategories;
    }

    public void setAds(HashMap<String, Integer> ads) {
        this.ads = ads;
    }

    public void setSubCategories(HashMap<String, Boolean> subCategories) {
        this.subCategories = subCategories;
    }
}
