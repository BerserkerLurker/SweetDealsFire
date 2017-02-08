package com.kallinikos.tech.sweetdealsfire.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kallinikos.tech.sweetdealsfire.R;
import com.kallinikos.tech.sweetdealsfire.models.AdImage;
import com.kallinikos.tech.sweetdealsfire.models.Category;

import java.util.List;

/**
 * Created by kallinikos on 25/01/17.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private static final String TAG = CategoryAdapter.class.getSimpleName();

    private List<Category> mData;
    private LayoutInflater mInflater;

    private static  ClickListener clickListener;

    public List<Category> getmData() {
        return mData;
    }
    public CategoryAdapter(Context context,List<Category> data){
        this.mData = data;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.cat_list_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Category currentObj = mData.get(position);
        holder.setData(currentObj);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    //******************MyViewHolder******************
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title;
        ImageView imgThumb;
        Context context;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.txv_row);
            imgThumb = (ImageView)itemView.findViewById(R.id.img_row);
            context = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        public void setData(Category current){
            this.title.setText(current.getTitle());
            //this.imgThumb.setImageResource(current.getImageId());

            Glide.with(context)
                    .load(current.getImageId())
                    .into(this.imgThumb);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener){
        CategoryAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
