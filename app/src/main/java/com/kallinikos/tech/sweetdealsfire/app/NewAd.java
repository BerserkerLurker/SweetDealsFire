package com.kallinikos.tech.sweetdealsfire.app;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kallinikos.tech.sweetdealsfire.R;
import com.kallinikos.tech.sweetdealsfire.adapters.NewAdAdapter;
import com.kallinikos.tech.sweetdealsfire.dbmodels.Ad;
import com.kallinikos.tech.sweetdealsfire.models.AdImage;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewAd extends Fragment {
    private NewAdAdapter adapter;

    private TextView titletxt;
    private TextView desctxt;
    private TextView pricetxt;

    private Spinner catSpinner;
    private Spinner subSpinner;

    private Button newAdButton;

    private static int PICK_IMAGE=1;

    private DatabaseReference mDatabase;
    private String userId;

    public NewAd() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_ad, container, false);
        //Recover UserId from Main
        Bundle bundle = this.getArguments();
        if (bundle != null){
            userId = bundle.getString("Uid");
            //Toast.makeText(this.getContext(),userId,Toast.LENGTH_SHORT).show();
        }

        //Init Firebase Database Ref
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Images RecyclerView Setup
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.new_ad_recyclerview);
        adapter = new NewAdAdapter(this.getActivity(), AdImage.getData());
        recyclerView.setAdapter(adapter);

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this.getActivity(), 3);
        recyclerView.setLayoutManager(mGridLayoutManager);

        //Adapter onClickListener for image selection from gallery
        adapter.setOnItemClickListener(new NewAdAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (position == 0) {
                    //Toast.makeText(v.getContext(),"add image",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                }else{
                    adapter.updateAdapterDel(position);
                }
            }

            @Override
            public void onItemLongClick(int position, View v) {

            }
        });

        //Title
        titletxt = (TextView)view.findViewById(R.id.input_title);
        //Description
        desctxt = (TextView)view.findViewById(R.id.input_desc);
        //Price
        pricetxt = (TextView)view.findViewById(R.id.input_price);
        //Category Spinner
        catSpinner = (Spinner) view.findViewById(R.id.category_spin);
        //Sub-Category Spinner
        subSpinner = (Spinner) view.findViewById(R.id.sub_category_spin);

        //Populate Category Spinner From String Array
        ArrayAdapter<CharSequence> catAdapter = ArrayAdapter.createFromResource(
                this.getActivity(),
                R.array.categories,
                R.layout.support_simple_spinner_dropdown_item
        );

        catAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        catSpinner.setAdapter(catAdapter);

        //Change Sub-Category Spinner depending on User choice fron Category Spinner
        catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayAdapter<CharSequence> subAdapter;
                switch (i){
                    case 0:
                        //Toast.makeText(adapterView.getContext(),adapterView.getItemAtPosition(i).toString(),Toast.LENGTH_SHORT).show();

                        subAdapter = ArrayAdapter.createFromResource(
                                adapterView.getContext(),
                                R.array.sub_categories_fashion,
                                R.layout.support_simple_spinner_dropdown_item
                        );

                        subAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        subSpinner.setAdapter(subAdapter);break;


                    case 1:
                        //Toast.makeText(adapterView.getContext(),adapterView.getItemAtPosition(i).toString(),Toast.LENGTH_SHORT).show();

                        subAdapter = ArrayAdapter.createFromResource(
                                adapterView.getContext(),
                                R.array.sub_categories_Sports,
                                R.layout.support_simple_spinner_dropdown_item
                        );

                        subAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        subSpinner.setAdapter(subAdapter);break;


                    case 2:
                        //Toast.makeText(adapterView.getContext(),adapterView.getItemAtPosition(i).toString(),Toast.LENGTH_SHORT).show();

                        subAdapter = ArrayAdapter.createFromResource(
                                adapterView.getContext(),
                                R.array.sub_categories_electronics,
                                R.layout.support_simple_spinner_dropdown_item
                        );

                        subAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        subSpinner.setAdapter(subAdapter);break;


                    case 3:
                        //Toast.makeText(adapterView.getContext(),adapterView.getItemAtPosition(i).toString(),Toast.LENGTH_SHORT).show();

                        subAdapter = ArrayAdapter.createFromResource(
                                adapterView.getContext(),
                                R.array.sub_categories_motors,
                                R.layout.support_simple_spinner_dropdown_item
                        );

                        subAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        subSpinner.setAdapter(subAdapter);break;


                    case 4:
                        //Toast.makeText(adapterView.getContext(),adapterView.getItemAtPosition(i).toString(),Toast.LENGTH_SHORT).show();

                        subAdapter = ArrayAdapter.createFromResource(
                                adapterView.getContext(),
                                R.array.sub_categories_collectibles,
                                R.layout.support_simple_spinner_dropdown_item
                        );

                        subAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        subSpinner.setAdapter(subAdapter);break;


                    case 5:
                        //Toast.makeText(adapterView.getContext(),adapterView.getItemAtPosition(i).toString(),Toast.LENGTH_SHORT).show();

                        subAdapter = ArrayAdapter.createFromResource(
                                adapterView.getContext(),
                                R.array.sub_categories_home,
                                R.layout.support_simple_spinner_dropdown_item
                        );

                        subAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        subSpinner.setAdapter(subAdapter);break;


                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Button Listener
        newAdButton = (Button)view.findViewById(R.id.btn_create_ad);
        newAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                String catval = catSpinner.getSelectedItem().toString();
                String subval = subSpinner.getSelectedItem().toString();
                String titleval = titletxt.getText().toString();
                String descval = desctxt.getText().toString();
                int priceval = Integer.parseInt(pricetxt.getText().toString());
                final List<AdImage> imglist = adapter.getmData();
                //if (imglist.size()>1){
                    imglist.remove(0);
                //}
                String Uid = userId;
                final HashMap<String,String> imgNames = new HashMap<>();
                //----------Firebase----------
                DatabaseReference mRef = mDatabase.child("ads").push();
                String key = mRef.getKey();

                if (imglist.size()!=0) {
                    for (int i = 0; i < imglist.size(); i++) {
                        final StorageReference mStorage = FirebaseStorage.getInstance().getReference().child(key).child(imglist.get(i).getTitle());

                        imgNames.put(imglist.get(i).getTitle().substring(0, imglist.get(i).getTitle().indexOf(".")), imglist.get(i).getTitle());

                        mStorage.putFile(Uri.parse(imglist.get(i).getImageId())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.w("Success-----", "Upload Success : "+mStorage.getPath());
                                //Toast.makeText(getActivity().getApplicationContext(), "Upload Success : "+mStorage.getPath(), Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Fail-----", e);
                            }
                        });
                    }
                }
                //Ad
                Ad ad = new Ad(titleval,descval,priceval,imgNames,Uid,catval,subval);

                mRef.setValue(ad);
                //Category update
                DatabaseReference mRefcat = mDatabase.child("categories").child(catval.toLowerCase()).child("ads");
                Map<String,Object> catUpdate = new HashMap<String, Object>();
                catUpdate.put(key,priceval);
                mRefcat.updateChildren(catUpdate);

                //Subcategory update
                DatabaseReference mRefsub = mDatabase.child("subcategories").child(subval.toLowerCase());
                Map<String,Object> subUpdate = new HashMap<String, Object>();
                subUpdate.put(key,priceval);
                mRefsub.child("ads").updateChildren(subUpdate);
                mRefsub.child("category").setValue(catval);

                //User ads update
                DatabaseReference mRefUser = mDatabase.child("users").child(Uid).child("ads");
                Map<String,Object> userUpdate = new HashMap<String, Object>();
                userUpdate.put(key,priceval);
                mRefUser.updateChildren(userUpdate);

                //Toast.makeText(getActivity().getBaseContext(),key,Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).categories();
                //----------Firebase----------


            }
        });

        return view;
    }

    //Image picker onActivityResult
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            Uri selectedImageUri = data.getData();
            String realImagePath = selectedImageUri.toString();
            //String realImagePath = getRealPathFromURI(selectedImageUri);
            String ext = getContext().getContentResolver().getType(selectedImageUri);
            adapter.updateAdapter(realImagePath,ext);
        }
    }

        /*public String getRealPathFromURI(Uri uri) {
            String[] projection = { MediaStore.Images.Media.DATA };
            @SuppressWarnings("deprecation")
            Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }*/

}
