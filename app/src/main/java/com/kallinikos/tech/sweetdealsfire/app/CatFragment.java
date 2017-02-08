package com.kallinikos.tech.sweetdealsfire.app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kallinikos.tech.sweetdealsfire.R;
import com.kallinikos.tech.sweetdealsfire.adapters.CategoryAdapter;
import com.kallinikos.tech.sweetdealsfire.models.Category;

import java.util.List;

import static com.kallinikos.tech.sweetdealsfire.R.id.recyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class CatFragment extends Fragment {


    public CatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cat, container, false);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        CategoryAdapter adapter = new CategoryAdapter(this.getActivity(), Category.getData());
        recyclerView.setAdapter(adapter);

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this.getActivity(), 2);
        recyclerView.setLayoutManager(mGridLayoutManager);

        final List<Category> mData = adapter.getmData();


        adapter.setOnItemClickListener(new CategoryAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                ((MainActivity)v.getContext()).adsListing(mData.get(position).getTitle());
            }
        });

        return view;
    }

}
