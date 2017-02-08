package com.kallinikos.tech.sweetdealsfire.app;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kallinikos.tech.sweetdealsfire.R;
import com.kallinikos.tech.sweetdealsfire.adapters.AdsListingAdapter;
import com.kallinikos.tech.sweetdealsfire.adapters.NewAdAdapter;
import com.kallinikos.tech.sweetdealsfire.dbmodels.Ad;
import com.kallinikos.tech.sweetdealsfire.dbmodels.Category;
import com.kallinikos.tech.sweetdealsfire.dbmodels.SubCategory;
import com.kallinikos.tech.sweetdealsfire.models.AdImage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdsFragment extends Fragment implements Callback{
    private static final String TAG = AdsFragment.class.getSimpleName();


    private AdsListingAdapter adapter;
    private Spinner subSpinner;

    private String userId;
    private String catClicked;

    final private DatabaseReference mref = FirebaseDatabase.getInstance().getReference();;
    private int choice=0;

    private TextView title;
    private TextView price;

    public AdsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ads, container, false);
        //Recover UserId from Main
        Bundle bundle = this.getArguments();
        if (bundle != null){
            userId = bundle.getString("Uid");
            catClicked = bundle.getString("Cat");
            //Toast.makeText(this.getContext(),userId+"----"+catClicked,Toast.LENGTH_SHORT).show();

        }



        //-----------------Populate Spinner Begin-----------------
        subSpinner = (Spinner)view.findViewById(R.id.ads_sub_category_spin);

        ArrayAdapter<CharSequence> subAdapter;
        if (catClicked.equals("Fashion")){
            String[] sub = addAll(R.array.sub_categories_fashion);
            subAdapter = new ArrayAdapter(
                                getContext(),
                                R.layout.support_simple_spinner_dropdown_item,
                                sub
                        );

            subAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            subSpinner.setAdapter(subAdapter);
        }else if (catClicked.equals("Sports")){
            String[] sub = addAll(R.array.sub_categories_Sports);
            subAdapter = new ArrayAdapter(
                    getContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    sub
            );

            subAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            subSpinner.setAdapter(subAdapter);
        }else if (catClicked.equals("Electronics")){
            String[] sub = addAll(R.array.sub_categories_electronics);
            subAdapter = new ArrayAdapter(
                    getContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    sub
            );

            subAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            subSpinner.setAdapter(subAdapter);
        }else if (catClicked.equals("Motors")){
            String[] sub = addAll(R.array.sub_categories_motors);
            subAdapter = new ArrayAdapter(
                    getContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    sub
            );

            subAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            subSpinner.setAdapter(subAdapter);
        }else if (catClicked.equals("Collectibles & Art")){
            String[] sub = addAll(R.array.sub_categories_collectibles);
            subAdapter = new ArrayAdapter(
                    getContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    sub
            );

            subAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            subSpinner.setAdapter(subAdapter);
        }else if (catClicked.equals("Home & Garden")){
            String[] sub = addAll(R.array.sub_categories_home);
            subAdapter = new ArrayAdapter(
                    getContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    sub
            );

            subAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            subSpinner.setAdapter(subAdapter);
        }

        //-----------------Populate Spinner End-----------------

        //Images RecyclerView Setup
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.adsrecyclerView);
        adapter = new AdsListingAdapter(this.getActivity(), new ArrayList<AdImage>());
        recyclerView.setAdapter(adapter);

        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(this.getActivity());
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManagerVertical);




        return view;
    }


    private String[] addAll(int subsNoAll){
        Resources res = getResources();
        String[] subsA = res.getStringArray(subsNoAll);
        List<String> subsL = new ArrayList<String>(Arrays.asList(subsA));
        subsL.add(0,"All");
        String[] subs = new String[subsL.size()];
        for (int i=0;i<subsL.size();i++){
            subs[i] = subsL.get(i);
        }
        return subs;
    }

    private Category adsc = new Category();
    private SubCategory adss = new SubCategory();

    public void setChoice(int choice) {
        this.choice = choice;
    }

    @Override
    public void onStart() {
        super.onStart();


        subSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 0) {
                    setChoice(1);
                    filterSpinSub();
                }if(i == 0){
                    setChoice(0);
                    filterSpinCat();
                }
                //Toast.makeText(getActivity().getBaseContext(),choice+"choice",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /*if(choice == 0){
            adapter.resetAdapter();
            filterSpinCat();
            adapter.notifyDataSetChanged();

        }*/

        adapter.setOnItemClickListener(new AdsListingAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Toast.makeText(v.getContext(),"Adapter Clicked "+v.getTag(),Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).adPage(v.getTag().toString());
            }
        });
    }

    //(@NonNull final Callback callback)
    public void filterSpinCat(){
        final DatabaseReference mref1 = mref.child("categories").child(catClicked.toLowerCase()).child("ads");
        ValueEventListener globalListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> keys = new ArrayList<>();

                for( DataSnapshot keysnap : dataSnapshot.getChildren()){
                    keys.add(keysnap.getKey());
                }

                //Toast.makeText(getActivity().getBaseContext(),keys.size()+"",Toast.LENGTH_SHORT).show();
                OnComplete(keys);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        //Toast.makeText(getContext(),adsc.getAds().size()+"",Toast.LENGTH_SHORT).show();

        mref1.addListenerForSingleValueEvent(globalListener);

    }

    //(@NonNull final Callback callback)
    public void filterSpinSub(){
        final DatabaseReference mref1 = mref.child("subcategories").child(subSpinner.getSelectedItem().toString().toLowerCase()).child("ads");
        ValueEventListener globalListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> keys = new ArrayList<>();

                for( DataSnapshot keysnap : dataSnapshot.getChildren()){
                    keys.add(keysnap.getKey());
                }

                //Toast.makeText(getActivity().getBaseContext(),keys.size()+"",Toast.LENGTH_SHORT).show();
                OnComplete(keys);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mref1.addListenerForSingleValueEvent(globalListener);
    }

    @Override
    public void OnComplete(final List<String> keys) {
        Toast.makeText(getActivity().getBaseContext(),keys.size()+"",Toast.LENGTH_SHORT).show();
        adapter.resetAdapter();
        for(int i=0;i<keys.size();i++){
            String key = keys.get(i);
            final DatabaseReference mref2 = mref.child("ads");
            mref2.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
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

interface Callback{
    void OnComplete(List<String> keys);
}