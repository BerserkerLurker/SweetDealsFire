package com.kallinikos.tech.sweetdealsfire.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kallinikos.tech.sweetdealsfire.R;
import com.kallinikos.tech.sweetdealsfire.app.NewAd;
import com.kallinikos.tech.sweetdealsfire.models.AdImage;
import com.kallinikos.tech.sweetdealsfire.models.Category;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Created by kallinikos on 28/01/17.
 */

public class NewAdAdapter extends RecyclerView.Adapter<NewAdAdapter.MyViewHolder> {
    private static final String TAG = NewAdAdapter.class.getSimpleName();
    private List<AdImage> mData;
    private LayoutInflater mInflater;

    private static  ClickListener clickListener;

    public List<AdImage> getmData() {
        return mData;
    }



    public NewAdAdapter(Context context, List<AdImage> data){
        this.mData = data;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.images_list_item, parent, false);
        NewAdAdapter.MyViewHolder holder = new NewAdAdapter.MyViewHolder(view);
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

    public void updateAdapter(String uri, String ext){
        String title = UUID.randomUUID().toString().replace("-","")+"."+ext.substring(ext.lastIndexOf("/")+1);

        AdImage newImg = new AdImage();
        newImg.setTitle(title);
        newImg.setImageId(uri);

        mData.add(newImg);
        notifyDataSetChanged();
    }

    public void updateAdapterDel(int pos){
        mData.remove(pos);
        notifyDataSetChanged();
    }

    //******************MyViewHolder******************
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView title;
        ImageView imgThumb;

        Context context;

        public MyViewHolder(View itemView) {
            super(itemView);
            //title = (TextView)itemView.findViewById(R.id.txv_row_ad);
            imgThumb = (ImageView)itemView.findViewById(R.id.img_row_ad);
            context = itemView.getContext();

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setData(AdImage current){
           // this.title.setText(current.getTitle());
            if (current.getTitle().equals("init_add")){
                int id = context.getResources().getIdentifier(current.getImageId(), "drawable", context.getPackageName());
                Drawable drawable = context.getResources().getDrawable(id);

                //this.imgThumb.setImageDrawable(drawable);
                Glide.with(context)
                        .load(id)
                        .into(this.imgThumb);
            }else {
                Toast.makeText(context,current.getImageId(),Toast.LENGTH_LONG).show();

                String assetPath = "file://"+current.getImageId();
                //Uri.parse(assetPath)
                Glide.with(context).load(Uri.parse(current.getImageId())).into(this.imgThumb);
            }

        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onItemLongClick(getAdapterPosition(), view);
            return false;
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        NewAdAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }
}
