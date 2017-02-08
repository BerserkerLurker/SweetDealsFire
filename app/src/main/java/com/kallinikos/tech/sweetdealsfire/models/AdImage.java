package com.kallinikos.tech.sweetdealsfire.models;

import com.kallinikos.tech.sweetdealsfire.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kallinikos on 28/01/17.
 */

public class AdImage {
    private String key;
    private String title;
    private String imageId;
    private int price;
    private String desc;

    public AdImage() {
    }

    public AdImage(String title, String imageId) {
        this.title = title;
        this.imageId = imageId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static List<AdImage> getData(){
        List<AdImage> data = new ArrayList<>();
        String[] images = {
                "picture"
        };
        String[] Images = {"init_add"};

        for (int i=0;i<images.length;i++){
            AdImage current = new AdImage();
            current.setTitle(Images[i]);
            current.setImageId(images[i]);
            data.add(current);
        }
        return data;
    }

    public static List<AdImage> getData2(){
        List<AdImage> data = new ArrayList<>();
        String[] images = {
                "picture"
        };
        String[] keys = {"blakey"};
        String[] Images = {"init_add"};
        int[] prices = {1000};
        String[] descs = {"Desc first line"};

        for (int i=0;i<images.length;i++){
            AdImage current = new AdImage();
            current.setKey(keys[i]);
            current.setTitle(Images[i]);
            current.setImageId(images[i]);
            current.setPrice(prices[i]);
            current.setDesc(descs[i]);
            data.add(current);
        }
        return data;
    }
    /*public static List<AdImage> addData(List<AdImage> old,String title,String url){
        List<AdImage> newList = old;
        AdImage newImage = new AdImage();
        newImage.setTitle(title);
        newImage.setImageId(url);
        newList.add(newImage);

        return newList;
    }*/
}
