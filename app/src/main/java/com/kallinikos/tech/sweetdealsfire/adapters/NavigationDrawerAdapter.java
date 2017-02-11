package com.kallinikos.tech.sweetdealsfire.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kallinikos.tech.sweetdealsfire.R;
import com.kallinikos.tech.sweetdealsfire.app.MainActivity;
import com.kallinikos.tech.sweetdealsfire.models.NavigationDrawerItem;

import java.util.Collections;
import java.util.List;

/**
 * Created by kallinikos on 13/10/16.
 */

public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder>{

    private List<NavigationDrawerItem> mDataList = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public NavigationDrawerAdapter(Context context, List<NavigationDrawerItem> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.mDataList = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_list_item,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        NavigationDrawerItem current = mDataList.get(position);

        holder.imgIcon.setImageResource(current.getImageId());
        holder.title.setText(current.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tab = holder.title.getText().toString();
                Toast.makeText(context, tab, Toast.LENGTH_SHORT).show();
                if (tab.equals("Home")){
                    //categories
                    if (holder.mcontext.getClass()==MainActivity.class){
                        ((MainActivity)holder.mcontext).getDrawerLayout().closeDrawer(Gravity.LEFT);

                        ((MainActivity)holder.mcontext).categories();
                    }
                }
                else if (tab.equals("Favorites")){
                    ((MainActivity)holder.mcontext).getDrawerLayout().closeDrawer(Gravity.LEFT);
                    String Uid =  ((MainActivity)holder.mcontext).getUid();
                    ((MainActivity)holder.mcontext).favs(Uid);

                }
                else if (tab.equals("My Ads")){
                    ((MainActivity)holder.mcontext).getDrawerLayout().closeDrawer(Gravity.LEFT);
                    String Uid =  ((MainActivity)holder.mcontext).getUid();

                    ((MainActivity)holder.mcontext).myads(Uid);
                }
                else if (tab.equals("New Ad")){
                    //newad
                    if (holder.mcontext.getClass()==MainActivity.class){
                        ((MainActivity)holder.mcontext).getDrawerLayout().closeDrawer(Gravity.LEFT);

                        ((MainActivity)holder.mcontext).newad();

                    }
                }
                else if (tab.equals("Sign out")){
                    //signout
                    if (holder.mcontext.getClass()==MainActivity.class){
                        ((MainActivity)holder.mcontext).getDrawerLayout().closeDrawer(Gravity.LEFT);

                        ((MainActivity)holder.mcontext).signout();

                    }

                }
                else if (tab.equals("About")){
                    ((MainActivity)holder.mcontext).getDrawerLayout().closeDrawer(Gravity.LEFT);

                }



            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView imgIcon;
        private final Context mcontext;

        public MyViewHolder(View itemView){
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title);
            imgIcon = (ImageView)itemView.findViewById(R.id.imgIcon);
            mcontext = itemView.getContext();


        }
    }
}
