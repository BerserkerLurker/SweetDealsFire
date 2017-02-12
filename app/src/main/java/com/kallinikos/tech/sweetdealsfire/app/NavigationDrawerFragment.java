package com.kallinikos.tech.sweetdealsfire.app;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.kallinikos.tech.sweetdealsfire.adapters.NavigationDrawerAdapter;
import com.kallinikos.tech.sweetdealsfire.models.NavigationDrawerItem;
import com.kallinikos.tech.sweetdealsfire.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment {
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;



    public NavigationDrawerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navigation_drawer,container,false);
        final ImageView userPic = (ImageView)view.findViewById(R.id.dwr_userpic);
        /*Glide.with(getContext())
                .using(new FirebaseImageLoader())
                .load(ref)
                .asBitmap()
                .placeholder(R.drawable.loadplaceholder)
                .animate(android.R.anim.slide_in_left)
                .error(R.drawable.noimage)
                .centerCrop()
                .into(new BitmapImageViewTarget(imageThumb) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        imageThumb.setImageDrawable(circularBitmapDrawable);
                    }});*/

        Glide.with(getContext())
                .load(R.drawable.bag)
                .asBitmap()
                .placeholder(R.drawable.users)
                .animate(android.R.anim.fade_in)
                .error(R.drawable.noimage)
                .centerCrop()
                .into(new BitmapImageViewTarget(userPic) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        userPic.setImageDrawable(circularBitmapDrawable);
                    }});



        setUpRecyclerView(view);
        return view;
    }

    private void setUpRecyclerView(View view) {
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.drawerList);

        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(getActivity(), NavigationDrawerItem.getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void setUpDrawer(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar){
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                ((MainActivity)getActivity()).closeSearchTab();
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //getActivity().findViewById(R.id.search_view).setVisibility(View.VISIBLE);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

}

