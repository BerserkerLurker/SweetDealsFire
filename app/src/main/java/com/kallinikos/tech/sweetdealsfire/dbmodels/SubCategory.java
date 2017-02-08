package com.kallinikos.tech.sweetdealsfire.dbmodels;

import java.util.HashMap;

/**
 * Created by kallinikos on 04/02/17.
 */

public class SubCategory {
    public String category;
    public HashMap<String,Integer> ads;

    public SubCategory(){

    }

    public SubCategory(String category, HashMap<String, Integer> ads){
        this.category = category;
        this.ads = ads;
    }

    public String getCategory() {
        return category;
    }

    public HashMap<String, Integer> getAds() {
        return ads;
    }
}
