package com.kallinikos.tech.sweetdealsfire.app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kallinikos.tech.sweetdealsfire.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAds extends Fragment {


    public MyAds() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_ads, container, false);
    }

}
