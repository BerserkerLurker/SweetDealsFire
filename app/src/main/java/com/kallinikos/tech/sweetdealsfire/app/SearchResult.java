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
import com.kallinikos.tech.sweetdealsfire.adapters.SearchResultAdapter;
import com.kallinikos.tech.sweetdealsfire.dbmodels.Ad;
import com.kallinikos.tech.sweetdealsfire.models.AdImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResult extends Fragment implements Callbacksearch{
    private static final String TAG = SearchResult.class.getSimpleName();

    private SearchResultAdapter adapter;
    private RecyclerView recyclerView;
    final private DatabaseReference mref = FirebaseDatabase.getInstance().getReference();

    private String query;

    public SearchResult() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if(bundle!=null){
            query = bundle.getString("query");
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);

        //Images RecyclerView Setup
        recyclerView = (RecyclerView) view.findViewById(R.id.searchrecyclerView);
        adapter = new SearchResultAdapter(this.getActivity(), new ArrayList<AdImage>());
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

        adapter.setOnItemClickListener(new SearchResultAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Toast.makeText(v.getContext(),"Adapter Clicked "+v.getTag(),Toast.LENGTH_SHORT).show();
                //TODO Create Edit Page
                ((MainActivity)getActivity()).adPage(v.getTag().toString());
            }
        });
    }

    public void setList(){
        final DatabaseReference mRefSearch = mref.child("ads");
        mRefSearch.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> keys = new ArrayList<>();
                for (DataSnapshot keysnap : dataSnapshot.getChildren()){
                    Ad ad = keysnap.getValue(Ad.class);
                    if ((ad.getTitle().toLowerCase().contains(query.toLowerCase()))||(ad.getDescription().toLowerCase().contains(query.toLowerCase()))){
                        keys.add(keysnap.getKey());
                    }

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
interface Callbacksearch{
    void OnComplete(List<String> keys);
}