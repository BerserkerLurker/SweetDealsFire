package com.kallinikos.tech.sweetdealsfire.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kallinikos.tech.sweetdealsfire.R;
import com.kallinikos.tech.sweetdealsfire.app.AdPage;
import com.kallinikos.tech.sweetdealsfire.models.AdImage;

import java.util.List;

/**
 * Created by kallinikos on 06/02/17.
 */

public class AdPageAdapter extends RecyclerView.Adapter<AdPageAdapter.MyViewHolder>{
    private static final String TAG = AdPageAdapter.class.getSimpleName();
    private List<AdImage> mData;
    private LayoutInflater mInflater;

    private static AdPageAdapter.ClickListener clickListener;

    public List<AdImage> getmData(){
        return mData;
    }

    public AdPageAdapter(Context context, List<AdImage> data){
        this.mData = data;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.ad_page_images_list_item, parent, false);
        AdPageAdapter.MyViewHolder holder = new AdPageAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AdImage currentObj = mData.get(position);
        holder.setData(currentObj);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    //-------------------MyViewHolder-------------------
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView img;
        Context context;

        public MyViewHolder(View itemView) {
            super(itemView);
            img = (ImageView)itemView.findViewById(R.id.img_row_ad);
            context = itemView.getContext();

            itemView.setOnClickListener(this);

        }

        public void setData(AdImage current){
            if ((current.getTitle() != null)&&(current.getTitle().equals("init_add"))){
                int id = context.getResources().getIdentifier(current.getImageId(), "drawable", context.getPackageName());
                Drawable drawable = context.getResources().getDrawable(id);

                //this.img.setImageDrawable(drawable);
                Glide.with(context)
                        .load(id)
                        .centerCrop()
                        .into(this.img);
            }else if(current.getImageId()!=null){
                StorageReference ref = FirebaseStorage.getInstance().getReference().child(current.getImageId());
                Glide.with(context)
                        .using(new FirebaseImageLoader())
                        .load(ref)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .placeholder(R.drawable.loadplaceholder)
                        .animate(android.R.anim.slide_in_left)
                        .error(R.drawable.noimage)
                        .centerCrop()
                        .into(this.img);
            }else if(current.getImageId()==null){
                Glide.with(context)
                        .load(R.drawable.noimage)
                        .centerCrop()
                        .into(this.img);
            }

        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }
    }
    public void setOnItemClickListener(ClickListener clickListener) {
        AdPageAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
