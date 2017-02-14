package com.kallinikos.tech.sweetdealsfire.app;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kallinikos.tech.sweetdealsfire.R;
import com.kallinikos.tech.sweetdealsfire.adapters.AdPageAdapter;
import com.kallinikos.tech.sweetdealsfire.dbmodels.Ad;
import com.kallinikos.tech.sweetdealsfire.dbmodels.User;
import com.kallinikos.tech.sweetdealsfire.models.AdImage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdPage extends Fragment {

    private DatabaseReference mref;
    private String key;
    private String uid;

    private TextView title;
    private TextView advertiser;
    private TextView timestamp;
    private TextView price;
    private TextView desc;
    private Button callBtn;
    private ImageView imageThumb;
    private FloatingActionButton favBtn;

    private AdPageAdapter adapter;
    private RecyclerView recyclerView;



    private List<AdImage> imgListFull;


    public AdPage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        if (bundle != null){
            key = bundle.getString("Key");
            uid = bundle.getString("Uid");
            //Toast.makeText(this.getContext(),userId,Toast.LENGTH_SHORT).show();
        }
        mref = FirebaseDatabase.getInstance().getReference();
        View view = inflater.inflate(R.layout.fragment_ad_page, container, false);

        title = (TextView)view.findViewById(R.id.ad_page_title);
        imageThumb = (ImageView) view.findViewById(R.id.ad_page_image);
        advertiser = (TextView)view.findViewById(R.id.ad_page_user);
        timestamp = (TextView)view.findViewById(R.id.ad_page_time);
        price = (TextView)view.findViewById(R.id.ad_page_price);
        desc = (TextView)view.findViewById(R.id.ad_page_desc);
        callBtn = (Button)view.findViewById(R.id.ad_page_call);
        favBtn = (FloatingActionButton)view.findViewById(R.id.ad_page_fav_fab);
        recyclerView = (RecyclerView) view.findViewById(R.id.ad_page_recycler);

        setFromAds(key, uid);


        return view;
    }

    private void setFromAds(final String key, final String uid){
        final DatabaseReference adRef = mref.child("ads").child(key);
        adRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Ad ad = dataSnapshot.getValue(Ad.class);

                title.setText(ad.getTitle());

                desc.setText(ad.getDescription());
                desc.setMovementMethod(new ScrollingMovementMethod());

                price.setText(ad.getPrice()+" DnT");

                timestamp.setText(timeFromStamp(ad.getTimestampCreatedLong()));

                setFromUsers(ad.getUser());

                setFav(uid,key,ad.getUser());

                //Toolbar name as cat > subcat
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(ad.getCategory()+" > "+ad.getSubcategory());

                if (ad.getImgNames() != null){
                    List<AdImage> imgList = new ArrayList<AdImage>();
                    for(DataSnapshot images : dataSnapshot.child("imgNames").getChildren()) {
                        String img = images.getValue().toString();
                        imgList.add(new AdImage(img.substring(0,img.indexOf(".")),key+"/"+img));
                    }
                    adapter = new AdPageAdapter(getActivity(), imgList);
                    recyclerView.setAdapter(adapter);
                    LinearLayoutManager mLinearLayoutManagerHorizontal = new LinearLayoutManager(getActivity());
                    mLinearLayoutManagerHorizontal.setOrientation(LinearLayoutManager.HORIZONTAL);
                    recyclerView.setLayoutManager(mLinearLayoutManagerHorizontal);

                    //TESSSSSSSSSSSSSSSSSSSSST
                    setImgList(imgList);

                    adapter.setOnItemClickListener(new AdPageAdapter.ClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            ((MainActivity)getActivity()).fullscreen(imgListFull, position);

                        }
                    });


                    StorageReference ref = FirebaseStorage.getInstance().getReference().child(imgList.get(0).getImageId());
                    /*Glide.with(getContext())
                            .using(new FirebaseImageLoader())
                            .load(ref)
                            .placeholder(R.drawable.loadplaceholder)
                            .animate(android.R.anim.slide_in_left)
                            .error(R.drawable.noimage)
                            .centerCrop()
                            .into(imageThumb);
                    */
                    //------Load image rounded-------
                    Glide.with(getContext())
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
                        }
                    });
                    //------Load image rounded-------


                }else {
                    recyclerView.setVisibility(View.GONE);
                    Glide.with(getContext())
                            .load(R.drawable.noimage)
                            .centerCrop()
                            .into(imageThumb);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String timeFromStamp(long stamp){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String timeVal = formatter.format(new Date(stamp));
        return timeVal;
    }

    private void setUpDial(final String phone){
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",phone,null));
                startActivity(dialIntent);

            }
        });
    }

    private void setFromUsers(String userID){
        final DatabaseReference userRef = mref.child("users").child(userID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                advertiser.setText(user.getDisplayName());
                callBtn.setText(user.getPhoneNumber());
                setUpDial(user.getPhoneNumber());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setFav(final String currentUser, final String adKey, final String advertiser){
        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference accountRef = mref.child("users").child(currentUser).child("favs");
                Map<String,Object> adAdvertiser = new HashMap<String, Object>();
                adAdvertiser.put(adKey,advertiser);
                accountRef.updateChildren(adAdvertiser).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(),"Added to Favorites",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }



    //TESSSSSSSSSSSSSSSSSSSSST
    private void setImgList(List<AdImage> imgList){
        imgListFull = imgList;
    }

}
