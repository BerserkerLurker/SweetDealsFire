package com.kallinikos.tech.sweetdealsfire.app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kallinikos.tech.sweetdealsfire.R;
import com.kallinikos.tech.sweetdealsfire.adapters.FavsListAdapter;
import com.kallinikos.tech.sweetdealsfire.dbmodels.Ad;
import com.kallinikos.tech.sweetdealsfire.models.AdImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavsListing extends Fragment implements Callbackfavs{
    private static final String TAG = AdsFragment.class.getSimpleName();
    private String userId;
    private FavsListAdapter adapter;
    final private DatabaseReference mref = FirebaseDatabase.getInstance().getReference();

    public FavsListing() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favs_listing, container, false);

        //Recover UserId from Main
        Bundle bundle = this.getArguments();
        if (bundle != null){
            userId = bundle.getString("Uid");

        }

        //Images RecyclerView Setup
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.favsrecyclerView);
        adapter = new FavsListAdapter(this.getActivity(), new ArrayList<AdImage>());
        recyclerView.setAdapter(adapter);

        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(this.getActivity());
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManagerVertical);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setList();

        adapter.setOnItemClickListener(new FavsListAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Toast.makeText(v.getContext(),"Adapter Clicked "+v.getTag(),Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).adPage(v.getTag().toString());
            }
        });
    }

    public void setList(){
        final DatabaseReference mRefFavs = mref.child("users").child(userId).child("favs");
        mRefFavs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> keys = new ArrayList<>();
                for (DataSnapshot keysnap : dataSnapshot.getChildren()){
                    keys.add(keysnap.getKey());
                }

                OnComplete(keys);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void OnComplete(List<String> keys) {
        Toast.makeText(getActivity().getBaseContext(),keys.size()+"",Toast.LENGTH_SHORT).show();

        adapter.resetAdapter();

        for (int i=0;i<keys.size();i++){
            String key = keys.get(i);
            final DatabaseReference mrefAds = mref.child("ads");
            mrefAds.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Ad ad = dataSnapshot.getValue(Ad.class);
                    //Toast.makeText(getActivity().getBaseContext(), dataSnapshot.getKey() + "", Toast.LENGTH_SHORT).show();

                    List<String> listImgURL = new ArrayList<String>();

                    if(ad.getImgNames()!=null){
                        HashMap<String,String> hash = ad.getImgNames();
                        for ( Map.Entry<String, String> entry : hash.entrySet()) {
                            String name = entry.getKey();
                            String nameExt = entry.getValue();
                            String imgURL = "/"+dataSnapshot.getKey()+"/"+nameExt;
                            listImgURL.add(imgURL);
                        }
                    }
                    adapter.updateAdapter(dataSnapshot.getKey(),ad.getTitle(),ad.getPrice(),ad.getDescription(),listImgURL);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}

interface Callbackfavs{
    void OnComplete(List<String> keys);
}