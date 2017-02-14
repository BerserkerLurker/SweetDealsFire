package com.kallinikos.tech.sweetdealsfire.app;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kallinikos.tech.sweetdealsfire.R;
import com.kallinikos.tech.sweetdealsfire.dbmodels.User;

import java.util.ArrayList;
import java.util.List;

import br.com.mauker.materialsearchview.MaterialSearchView;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView userpic;
    private TextView username;
    private RecyclerView recyclerView;
    private DrawerLayout drawerLayout;

    //*********
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRef;
    private String name;
    private String Uid;

    private MaterialSearchView searchView;
    private Bundle savedInstanceState;

    private static final String TAG="Main";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_main);

        //User Name & Picture NavDrawer
        userpic = (ImageView)findViewById(R.id.dwr_userpic);
        username = (TextView)findViewById(R.id.dwr_username);

        //Toolbar Setup
        setUpToolbar();

        //NavDrawer Setup
        setUpDrawer();

        //Home Setup
        categories();

        //----------------Firebase----------------
        mAuth = FirebaseAuth.getInstance();
        Uid = mAuth.getCurrentUser().getUid();
        if (Uid != null){
            mRef = FirebaseDatabase.getInstance().getReference().child("users").child(Uid);
        }



        //----------------
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.displayName);
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mRef.addValueEventListener(postListener);
        //----------------Firebase----------------


        searchView = (MaterialSearchView)findViewById(R.id.search_view);
        final List<String> suggestions = new ArrayList<>();
        suggestions.add("Car");
        suggestions.add("T-shirt");
        suggestions.add("Shoes");
        //searchView.addSuggestions(suggestions);
        searchView.clearSuggestions();

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String)parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),selection,Toast.LENGTH_SHORT).show();
                search(selection);
                searchView.closeSearch();
            }
        });


        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getApplicationContext(),"Enter",Toast.LENGTH_SHORT).show();
                search(query);
                searchView.closeSearch();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 1) {
                    //Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_SHORT).show();
                    searchView.addSuggestions(suggestions);

                }

                return true;
            }
        });


        //newad();
        //adsListing("test");

        //setUpRecyclerView();
        //setNewAdUpRecyclerView();



    }

    public String getUid() {
        return Uid;
    }

    //Setup Category Fragment and Close other in Home
   /* public void categories(){
        Fragment fragment = new CatFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.catFrame, fragment, "catFragment").addToBackStack(null).commit();
    }*/

    public void profile(String Uid){
        if (findViewById(R.id.fragment_container) != null){
            if (savedInstanceState != null){
                return;
            }

            toolbar.setTitle("Profile Settings");

            ProfileSettings profileSettings = new ProfileSettings();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileSettings).commit();
        }
    }

    public void categories(){
        if (findViewById(R.id.fragment_container) != null){
            if (savedInstanceState != null){
                return;
            }

            toolbar.setTitle("Home");

            CatFragment catFragment = new CatFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, catFragment).commit();
        }
    }

    public void newad(){
        if (findViewById(R.id.fragment_container) != null){
            if (savedInstanceState != null){
                return;
            }

            toolbar.setTitle("Create a new Ad");

            NewAd newAd = new NewAd();
            //Passing the Uid of logged in user via bundle to newAd fragment.
            Bundle bundle = new Bundle();
            bundle.putString("Uid",Uid);
            newAd.setArguments(bundle);


            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newAd).commit();
        }
    }

    public void adsListing(String cat){
        if (findViewById(R.id.fragment_container) != null){
            if (savedInstanceState != null){
                return;
            }
            toolbar.setTitle(cat +" Ads");

            AdsFragment adsFragment = new AdsFragment();
            //Passing the Uid of logged in user via bundle to ads fragment.
            Bundle bundle = new Bundle();
            bundle.putString("Uid",Uid);
            bundle.putString("Cat",cat);
            adsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, adsFragment).commit();
        }
    }

    public void adPage(String key){
        if (findViewById(R.id.fragment_container) != null){
            if (savedInstanceState != null){
                return;
            }

            AdPage adPage = new AdPage();
            //Passing the Uid of logged in user via bundle to ads fragment.
            Bundle bundle = new Bundle();
            bundle.putString("Uid",Uid);
            bundle.putString("Key",key);
            adPage.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, adPage).commit();
        }
    }

    public void favs(String Uid){
        if (findViewById(R.id.fragment_container) != null){
            if (savedInstanceState != null){
                return;
            }
            toolbar.setTitle("Favorites");

            FavsListing favsListing = new FavsListing();
            //Passing the Uid of logged in user via bundle to favs fragment.
            Bundle bundle = new Bundle();
            bundle.putString("Uid",Uid);
            favsListing.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, favsListing).commit();
        }
    }

    public void myads(String Uid){
        if (findViewById(R.id.fragment_container) != null){
            if (savedInstanceState != null){
                return;
            }

            toolbar.setTitle("My Ads");

            MyAds myAds = new MyAds();
            //Passing the Uid of logged in user via bundle to favs fragment.
            Bundle bundle = new Bundle();
            bundle.putString("Uid",Uid);
            myAds.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myAds).commit();
        }
    }

    public void search(String query){
        if (findViewById(R.id.fragment_container) != null){
            if (savedInstanceState != null){
                //String test = savedInstanceState.getString("query");
                return;
            }


            toolbar.setTitle("Search Results");

            SearchResult searchResult = new SearchResult();
            //Passing the Uid of logged in user via bundle to favs fragment.
            Bundle bundle = new Bundle();
            bundle.putString("query",query);
            searchResult.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, searchResult).commit();
        }
    }

    public void about(){
        if (findViewById(R.id.fragment_container) != null){
            if (savedInstanceState != null){
                return;
            }


            toolbar.setTitle("About");

            About about = new About();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, about).commit();
        }
    }


    /**

    public void categories(){
        findViewById(R.id.newAdFragment).setVisibility(View.GONE);
        findViewById(R.id.catFragment).setVisibility(View.VISIBLE);
        findViewById(R.id.adsListFragment).setVisibility(View.GONE);
        findViewById(R.id.adPageFragment).setVisibility(View.GONE);
        findViewById(R.id.favsListFragment).setVisibility(View.GONE);
        findViewById(R.id.myAdsListFragment).setVisibility(View.GONE);


        if(getSupportFragmentManager().findFragmentById(R.id.newAdFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.newAdFragment)).commit();
        }
        if(getSupportFragmentManager().findFragmentById(R.id.adsListFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.adsListFragment)).commit();
        }
        if(getSupportFragmentManager().findFragmentById(R.id.adPageFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.adPageFragment)).commit();
        }if(getSupportFragmentManager().findFragmentById(R.id.favsListFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.favsListFragment)).commit();
        }
        if(getSupportFragmentManager().findFragmentById(R.id.myAdsListFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.myAdsListFragment)).commit();
        }

        Fragment fragment = new CatFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.catFragment, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

    }

    //Setup NewAd Fragment and Close other in Home
    public void newad(){
        findViewById(R.id.catFragment).setVisibility(View.GONE);
        findViewById(R.id.newAdFragment).setVisibility(View.VISIBLE);
        findViewById(R.id.adsListFragment).setVisibility(View.GONE);
        findViewById(R.id.adPageFragment).setVisibility(View.GONE);
        findViewById(R.id.favsListFragment).setVisibility(View.GONE);
        findViewById(R.id.myAdsListFragment).setVisibility(View.GONE);


        if(getSupportFragmentManager().findFragmentById(R.id.catFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.catFragment)).commit();
        }
        if(getSupportFragmentManager().findFragmentById(R.id.adsListFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.adsListFragment)).commit();
        }
        if(getSupportFragmentManager().findFragmentById(R.id.adPageFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.adPageFragment)).commit();
        }if(getSupportFragmentManager().findFragmentById(R.id.favsListFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.favsListFragment)).commit();
        }
        if(getSupportFragmentManager().findFragmentById(R.id.myAdsListFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.myAdsListFragment)).commit();
        }

        Fragment fragment = new NewAd();

        //Passing the Uid of logged in user via bundle to newAd fragment.
        Bundle bundle = new Bundle();
        bundle.putString("Uid",Uid);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.newAdFragment, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

    }


    //Setup ads Fragment and Close other in Home
    public void adsListing(String cat){
       findViewById(R.id.catFragment).setVisibility(View.GONE);
        findViewById(R.id.newAdFragment).setVisibility(View.GONE);
        findViewById(R.id.adsListFragment).setVisibility(View.VISIBLE);
        findViewById(R.id.adPageFragment).setVisibility(View.GONE);
        findViewById(R.id.favsListFragment).setVisibility(View.GONE);
        findViewById(R.id.myAdsListFragment).setVisibility(View.GONE);


        if(getSupportFragmentManager().findFragmentById(R.id.catFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.catFragment)).commit();
        }
        if(getSupportFragmentManager().findFragmentById(R.id.newAdFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.newAdFragment)).commit();
        }
        if(getSupportFragmentManager().findFragmentById(R.id.adPageFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.adPageFragment)).commit();
        }if(getSupportFragmentManager().findFragmentById(R.id.favsListFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.favsListFragment)).commit();
        }
        if(getSupportFragmentManager().findFragmentById(R.id.myAdsListFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.myAdsListFragment)).commit();
        }

        Fragment fragment = new AdsFragment();

        //Passing the Uid of logged in user via bundle to ads fragment.
        Bundle bundle = new Bundle();
        bundle.putString("Uid",Uid);
        bundle.putString("Cat",cat);
        fragment.setArguments(bundle);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.adsListFragment, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
    }

    //Setup adpage Fragment and Close other in Home
    public void adPage(String key){
        findViewById(R.id.catFragment).setVisibility(View.GONE);
        findViewById(R.id.newAdFragment).setVisibility(View.GONE);
        findViewById(R.id.adsListFragment).setVisibility(View.GONE);
        findViewById(R.id.adPageFragment).setVisibility(View.VISIBLE);
        findViewById(R.id.favsListFragment).setVisibility(View.GONE);
        findViewById(R.id.myAdsListFragment).setVisibility(View.GONE);


        if(getSupportFragmentManager().findFragmentById(R.id.catFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.catFragment)).commit();
        }
        if(getSupportFragmentManager().findFragmentById(R.id.newAdFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.newAdFragment)).commit();
        }
        if(getSupportFragmentManager().findFragmentById(R.id.adsListFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.adsListFragment)).commit();
        }
        if(getSupportFragmentManager().findFragmentById(R.id.favsListFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.favsListFragment)).commit();
        }
        if(getSupportFragmentManager().findFragmentById(R.id.myAdsListFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.myAdsListFragment)).commit();
        }

        Fragment fragment = new AdPage();

        //Passing the Uid of logged in user via bundle to ads fragment.
        Bundle bundle = new Bundle();
        bundle.putString("Uid",Uid);
        bundle.putString("Key",key);
        fragment.setArguments(bundle);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.adPageFragment, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
    }

    //Setup favorites Fragment and Close other in Home
    public void favs(String Uid){
        findViewById(R.id.catFragment).setVisibility(View.GONE);
        findViewById(R.id.newAdFragment).setVisibility(View.GONE);
        findViewById(R.id.adsListFragment).setVisibility(View.GONE);
        findViewById(R.id.adPageFragment).setVisibility(View.GONE);
        findViewById(R.id.favsListFragment).setVisibility(View.VISIBLE);
        findViewById(R.id.myAdsListFragment).setVisibility(View.GONE);


        if(getSupportFragmentManager().findFragmentById(R.id.catFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.catFragment)).commit();
        }
        if(getSupportFragmentManager().findFragmentById(R.id.newAdFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.newAdFragment)).commit();
        }
        if(getSupportFragmentManager().findFragmentById(R.id.adsListFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.adsListFragment)).commit();
        }
        if(getSupportFragmentManager().findFragmentById(R.id.adPageFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.adPageFragment)).commit();
        }
        if(getSupportFragmentManager().findFragmentById(R.id.myAdsListFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.myAdsListFragment)).commit();
        }

        Fragment fragment = new FavsListing();

        //Passing the Uid of logged in user via bundle to favs fragment.
        Bundle bundle = new Bundle();
        bundle.putString("Uid",Uid);

        fragment.setArguments(bundle);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.favsListFragment, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
    }

    //Setup myads Fragment and Close other in Home
    public void myads(String Uid){
        findViewById(R.id.catFragment).setVisibility(View.GONE);
        findViewById(R.id.newAdFragment).setVisibility(View.GONE);
        findViewById(R.id.adsListFragment).setVisibility(View.GONE);
        findViewById(R.id.adPageFragment).setVisibility(View.GONE);
        findViewById(R.id.favsListFragment).setVisibility(View.GONE);
        findViewById(R.id.myAdsListFragment).setVisibility(View.VISIBLE);

        if(getSupportFragmentManager().findFragmentById(R.id.catFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.catFragment)).commit();
        }
        if(getSupportFragmentManager().findFragmentById(R.id.newAdFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.newAdFragment)).commit();
        }
        if(getSupportFragmentManager().findFragmentById(R.id.adsListFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.adsListFragment)).commit();
        }
        if(getSupportFragmentManager().findFragmentById(R.id.adPageFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.adPageFragment)).commit();
        }
        if(getSupportFragmentManager().findFragmentById(R.id.favsListFragment) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.favsListFragment)).commit();
        }

        Fragment fragment = new MyAds();

        //Passing the Uid of logged in user via bundle to favs fragment.
        Bundle bundle = new Bundle();
        bundle.putString("Uid",Uid);

        fragment.setArguments(bundle);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.myAdsListFragment, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
    }

    */


    //Sign out and back to login screen
    public void signout(){
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(MainActivity.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

    //Set toolbar
    private void setUpToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        //toolbar.inflateMenu(R.menu.menu_main);
    }

    //Setup Nav Drawer Fragment
    private void setUpDrawer() {

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.nav_drwr_fragment);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment.setUpDrawer(R.id.nav_drwr_fragment, drawerLayout, toolbar);
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    private void setUpRecyclerView(){
        /*recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        CategoryAdapter adapter = new CategoryAdapter(this, Category.getData());
        recyclerView.setAdapter(adapter);

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mGridLayoutManager);*/


    }

    private void setNewAdUpRecyclerView(){
        /*recyclerView = (RecyclerView)findViewById(R.id.new_ad_recyclerview);
        NewAdAdapter adapter = new NewAdAdapter(this, AdImage.getData());
        recyclerView.setAdapter(adapter);

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mGridLayoutManager);*/

    }

    //Prevents going back on mem stack
    @Override
    public void onBackPressed() {
        if(searchView.isOpen()){
            searchView.closeSearch();
        }else{
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }

    //Three dots menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    public void closeSearchTab(){
        searchView.closeSearch();
    }

    //Three dots menu listener
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String msg = "";

        switch (item.getItemId()){
            case R.id.search:
                msg = "Search";
                searchView.openSearch();
                searchView.bringToFront();
                break;

            case R.id.edit:
                msg = "Edit";
                break;

            case R.id.settings:
                msg = "Settings";
                break;

            case R.id.exit:
                msg = "Exit";
                break;
        }

        Toast.makeText(this, msg +" clicked!",Toast.LENGTH_SHORT).show();

        return super.onOptionsItemSelected(item);
    }
}
