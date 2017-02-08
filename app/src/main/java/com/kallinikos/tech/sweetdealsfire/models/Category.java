package com.kallinikos.tech.sweetdealsfire.models;

import com.kallinikos.tech.sweetdealsfire.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kallinikos on 25/01/17.
 */

public class Category {
    private String title;
    private int imageId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public static List<Category> getData(){
        List<Category> data = new ArrayList<>();
        int[] images = {
                R.drawable.bag,
                R.drawable.basketball_jersey,
                R.drawable.laptop,
                R.drawable.motorcycle,
                R.drawable.palette,
                R.drawable.rocking_chair
        };
        String[] Categories = {"Fashion", "Sports", "Electronics", "Motors", "Collectibles & Art", "Home & Garden"};

        for (int i=0;i<images.length;i++){
            Category current = new Category();
            current.setTitle(Categories[i]);
            current.setImageId(images[i]);
            data.add(current);
        }
        return data;
    }
}
