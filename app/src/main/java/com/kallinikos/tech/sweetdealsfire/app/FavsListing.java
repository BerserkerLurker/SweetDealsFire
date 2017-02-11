package com.kallinikos.tech.sweetdealsfire.app;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
    private static final String TAG = FavsListing.class.getSimpleName();
    private String userId;
    private FavsListAdapter adapter;
    private RecyclerView recyclerView;
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
        recyclerView = (RecyclerView) view.findViewById(R.id.favsrecyclerView);
        adapter = new FavsListAdapter(this.getActivity(), new ArrayList<AdImage>());
        recyclerView.setAdapter(adapter);

        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(this.getActivity());
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManagerVertical);

        setUpItemTouchHelper();
        setUpAnimationDecoratorHelper();
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
                    String key = keysnap.getKey();
                        keys.add(key);

                }



                OnComplete(keys);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void OnComplete(final List<String> keys) {
        Toast.makeText(getActivity().getBaseContext(),keys.size()+"",Toast.LENGTH_SHORT).show();

        adapter.resetAdapter();

        for (int i=0;i<keys.size();i++){
            final String key = keys.get(i);
            final DatabaseReference mrefAds = mref.child("ads");
            mrefAds.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue()!=null){
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
                    }else {
                        mref.child("users").child(userId).child("favs").child(key).removeValue();
                    }



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }


    /**
     * This is the standard support library way of implementing "swipe to delete" feature. You can do custom drawing in onChildDraw method
     * but whatever you draw will disappear once the swipe is over, and while the items are animating to their new position the recycler view
     * background will be visible. That is rarely an desired effect.
     */
    private void setUpItemTouchHelper(){

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            //Caching this once
            Drawable background;
            boolean initiated;

            private void init(){
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }
            //No moving motion only left swipe
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                if (adapter.isUndoOn() && adapter.isPendingRemoval(position)){
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPosition = viewHolder.getAdapterPosition();
                boolean undoOn = adapter.isUndoOn();
                if(undoOn){
                    adapter.pendingRemoval(swipedPosition);
                }else {
                    adapter.remove(swipedPosition);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if(viewHolder.getAdapterPosition() == -1){
                    //not interested in those
                    return;
                }

                if(!initiated){
                    init();
                }

                //draw red background
                background.setBounds(itemView.getRight() + (int)dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * We're gonna setup another ItemDecorator that will draw the red background in the empty space while the items are animating to thier new positions
     * after an item is removed.
     */
    private void setUpAnimationDecoratorHelper(){
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated){
                    init();
                }
                //only if animation is in progress
                if (parent.getItemAnimator().isRunning()){
                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time

                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    //this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    //this we need to find out
                    int top = 0;
                    int bottom = 0;

                    //find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for(int i=0; i<childCount; i++){
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);
                }


                super.onDraw(c, parent, state);
            }
        });
    }
}

interface Callbackfavs{
    void OnComplete(List<String> keys);
}