package com.kallinikos.tech.sweetdealsfire.app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kallinikos.tech.sweetdealsfire.R;
import com.kallinikos.tech.sweetdealsfire.models.AdImage;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FullScreenView extends Fragment {

    private List<AdImage> imgList;
    private int position;
    private ImageView imgFull;
    private RelativeLayout btleft;
    private RelativeLayout btright;

    public FullScreenView() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null){
            position = bundle.getInt("pos");
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_full_screen_view, container, false);

        int size = imgList.size();
        Toast.makeText(getContext(),size+"/"+position,Toast.LENGTH_SHORT).show();

        imgFull = (ImageView)view.findViewById(R.id.img_full);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.prog_bar);
        btleft = (RelativeLayout)view.findViewById(R.id.leftbt);
        btright = (RelativeLayout)view.findViewById(R.id.rightbt);


        int i = position;
        control(i, size);

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(imgList.get(position).getImageId());
        Glide.with(getContext())
                .using(new FirebaseImageLoader())
                .load(ref).listener(new RequestListener<StorageReference, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        })
                .animate(android.R.anim.slide_in_left)
                .centerCrop()
                .into(imgFull);



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

    private void control(int i, int size){
        if (i == 0) {
            btleft.setVisibility(View.GONE);
        }else {
            btleft.setVisibility(View.VISIBLE);
        }
        if (i == size-1){
            btright.setVisibility(View.GONE);
        }else {
            btright.setVisibility(View.VISIBLE);
        }
    }

    public void setImgList(List<AdImage> imgList){
        this.imgList = imgList;
    }
}
