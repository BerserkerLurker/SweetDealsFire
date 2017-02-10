package com.kallinikos.tech.sweetdealsfire.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kallinikos.tech.sweetdealsfire.R;
import com.kallinikos.tech.sweetdealsfire.app.MainActivity;
import com.kallinikos.tech.sweetdealsfire.models.AdImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kallinikos on 07/02/17.
 */

public class FavsListAdapter extends RecyclerView.Adapter<FavsListAdapter.MyViewHolder> {
    private static final String TAG = AdsListingAdapter.class.getSimpleName();


    private List<AdImage> mData;
    //--------
    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec
    private List<AdImage> itemsPendingRemoval;
    boolean undoOn;
    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<AdImage, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be
    //--------

    private Context mContext;
    private LayoutInflater mInflater;
    private static FavsListAdapter.ClickListener clickListener;

    public List<AdImage> getmData() {
        return mData;
    }

    public FavsListAdapter(Context context, List<AdImage> data){
        itemsPendingRemoval = new ArrayList<>();

        this.mContext = context;
        this.mData = data;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.fav_list_item, parent, false);
        FavsListAdapter.MyViewHolder holder = new FavsListAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        /*AdImage currentObj = mData.get(position);
        holder.setData(currentObj);*/

        final AdImage currentObj = mData.get(position);

        if (itemsPendingRemoval.contains(currentObj)){
            // we need to show the "undo" state of the row
            holder.itemView.setBackgroundColor(Color.RED);

            holder.imgThumb.setVisibility(View.GONE);
            holder.title.setVisibility(View.GONE);
            holder.desc.setVisibility(View.GONE);
            holder.price.setVisibility(View.GONE);

            holder.undoButton.setVisibility(View.VISIBLE);
            holder.undoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // user wants to undo the removal, let's cancel the pending task
                    Runnable pendingRemovalRunnable = pendingRunnables.get(currentObj);
                    pendingRunnables.remove(currentObj);
                    if(pendingRemovalRunnable != null){
                        handler.removeCallbacks(pendingRemovalRunnable);
                    }
                    itemsPendingRemoval.remove(currentObj);
                    // this will rebind the row in "normal" state
                    notifyItemChanged(mData.indexOf(currentObj));

                }
            });
        }else {
            // we need to show the "normal" state
            holder.itemView.setBackgroundColor(Color.WHITE);

            holder.imgThumb.setVisibility(View.VISIBLE);
            holder.title.setVisibility(View.VISIBLE);
            holder.desc.setVisibility(View.VISIBLE);
            holder.price.setVisibility(View.VISIBLE);
            holder.setData(currentObj);

            holder.undoButton.setVisibility(View.GONE);
            holder.undoButton.setOnClickListener(null);
        }

        //holder.setData(currentObj);
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }


    /*public void setUndoOn(boolean undoOn) {
        this.undoOn = undoOn;
    }*/

    public boolean isUndoOn() {
        return true;
        //return undoOn;
    }

    public void pendingRemoval(int position){
        final AdImage obj = mData.get(position);
        if(!itemsPendingRemoval.contains(obj)){
            itemsPendingRemoval.add(obj);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendinRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(mData.indexOf(obj));
                }
            };
            handler.postDelayed(pendinRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(obj, pendinRemovalRunnable);
        }
    }

    public void remove(int position){
        AdImage obj = mData.get(position);
        if(itemsPendingRemoval.contains(obj)){
            itemsPendingRemoval.remove(obj);
        }
        if(mData.contains(obj)){
            String key = obj.getKey();
            String uid = ((MainActivity)mContext).getUid();
            mData.remove(position);
            notifyItemRemoved(position);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("favs").child(key);
            ref.removeValue();
        }
    }

    public boolean isPendingRemoval(int position){
        AdImage obj = mData.get(position);
        return itemsPendingRemoval.contains(obj);
    }


    public void updateAdapter(String key,String title, int price, String desc, List<String> listImgURL){
        AdImage newCard = new AdImage();
        newCard.setKey(key);
        newCard.setTitle(title);
        if (listImgURL.size()>0){
            newCard.setImageId(listImgURL.get(0));
        }

        newCard.setPrice(price);
        newCard.setDesc(desc);

        mData.add(newCard);
        notifyDataSetChanged();
    }

    public void resetAdapter(){
        mData.clear();
        notifyDataSetChanged();
    }

    //-----------------------MyViewHolder-----------------------
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView price;
        TextView desc;
        ImageView imgThumb;
        Button undoButton;


        Context context;
        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.txv_favtitle);
            price = (TextView)itemView.findViewById(R.id.txv_favprice);
            desc = (TextView)itemView.findViewById(R.id.txv_favdesc) ;
            imgThumb = (ImageView)itemView.findViewById(R.id.img_favthumb);
            undoButton = (Button)itemView.findViewById(R.id.undo_button);
            context = itemView.getContext();

            itemView.setOnClickListener(this);
        }

        public void setData(AdImage current){
            this.title.setText(current.getTitle());
            this.itemView.setTag(current.getKey());
            this.price.setText(current.getPrice()+"");
            if (current.getDesc().length()>15){
                String aux = current.getDesc().substring(0,11)+"...";
                this.desc.setText(aux);
            }else{
                this.desc.setText(current.getDesc());
            }
            if ((current.getTitle() != null)&&(current.getTitle().equals("init_add"))) {
                int id = context.getResources().getIdentifier(current.getImageId(), "drawable", context.getPackageName());
                Drawable drawable = context.getResources().getDrawable(id);

                //this.imgThumb.setImageDrawable(drawable);

                Glide.with(context)
                        .load(id)
                        .centerCrop()
                        .into(this.imgThumb);
            }/*else if(current.getTitle() == null){
                int id = context.getResources().getIdentifier("noimage", "drawable", context.getPackageName());
                Drawable drawable = context.getResources().getDrawable(id);

                this.imgThumb.setImageDrawable(drawable);
            }*/else if(current.getImageId()!=null){
                //Toast.makeText(context,current.getImageId(),Toast.LENGTH_LONG).show();

                StorageReference ref = FirebaseStorage.getInstance().getReference().child(current.getImageId());
                Glide.with(context)
                        .using(new FirebaseImageLoader())
                        .load(ref)
                        .override(100,100)
                        .placeholder(R.drawable.loadplaceholder)
                        .animate(android.R.anim.slide_in_left)
                        .error(R.drawable.noimage)
                        .centerCrop()
                        .into(this.imgThumb);
            }else if(current.getImageId()==null){

                Glide.with(context)
                        .load(R.drawable.noimage)
                        .override(100,100)
                        .centerCrop()
                        .into(this.imgThumb);
            }

        }
        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        FavsListAdapter.clickListener = clickListener;
    }

    public interface ClickListener{
        void onItemClick(int position, View v);
    }
}
