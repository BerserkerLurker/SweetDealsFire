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
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kallinikos.tech.sweetdealsfire.R;
import com.kallinikos.tech.sweetdealsfire.models.AdImage;

import java.util.List;


/**
 * Created by kallinikos on 11/02/17.
 */

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.MyViewHolder>{
    private static final String TAG = SearchResultAdapter.class.getSimpleName();
    private List<AdImage> mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private static SearchResultAdapter.ClickListener clickListener;

    public List<AdImage> getmData() {
        return mData;
    }

    public SearchResultAdapter(Context context, List<AdImage> data){
        this.mContext = context;
        this.mData = data;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.search_result_list_item, parent, false);
        SearchResultAdapter.MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AdImage currentObj = mData.get(position);
        holder.setData(currentObj);
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
        //mData = AdImage.getData2();
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }


    //-----------------------MyViewHolder-----------------------
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title;
        TextView price;
        TextView desc;
        ImageView imgThumb;


        Context context;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.txv_search_title);
            price = (TextView)itemView.findViewById(R.id.txv_search_price);
            desc = (TextView)itemView.findViewById(R.id.txv_search_desc) ;
            imgThumb = (ImageView)itemView.findViewById(R.id.img_search_thumb);
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
                        .placeholder(R.drawable.loadplaceholder)
                        .animate(android.R.anim.slide_in_left)
                        .error(R.drawable.noimage)
                        .centerCrop()
                        .into(this.imgThumb);
            }else if(current.getImageId()==null){

                Glide.with(context)
                        .load(R.drawable.noimage)
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
        SearchResultAdapter.clickListener = clickListener;
    }

    public interface ClickListener{
        void onItemClick(int position, View v);
    }
}
