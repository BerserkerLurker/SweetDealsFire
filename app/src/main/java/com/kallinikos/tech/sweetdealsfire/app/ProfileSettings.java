package com.kallinikos.tech.sweetdealsfire.app;


import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kallinikos.tech.sweetdealsfire.R;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileSettings extends Fragment {
    FirebaseUser user;
    DatabaseReference mref;


    private ImageView profilePic;

    private RelativeLayout namePanel;
    private RelativeLayout namePanel2;
    private EditText nameInput;
    private Button confirmName;
    private ImageView p1;


    private RelativeLayout phonePanel;
    private RelativeLayout phonePanel2;
    private EditText phoneInput;
    private Button confirmPhone;
    private ImageView p2;

    private RelativeLayout emailPanel;
    private RelativeLayout emailPanel2;
    private EditText emailInput;
    private Button confirmEmail;
    private ImageView p3;

    private RelativeLayout passwordPanel;
    private RelativeLayout passwordPanel2;
    private EditText passwordInput;
    private Button confirmPassword;
    private ImageView p4;

    private Switch deleteSwitch;


    public ProfileSettings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_settings, container, false);

        profilePic = (ImageView) view.findViewById(R.id.profilepic);

        namePanel = (RelativeLayout) view.findViewById(R.id.namepanel);
        namePanel2 = (RelativeLayout) view.findViewById(R.id.namepanel2);
        nameInput = (EditText) view.findViewById(R.id.nameinput);
        confirmName = (Button) view.findViewById(R.id.confirmname);
        p1 = (ImageView) view.findViewById(R.id.p11);

        phonePanel = (RelativeLayout) view.findViewById(R.id.phonepanel);
        phonePanel2 = (RelativeLayout) view.findViewById(R.id.phonepanel2);
        phoneInput = (EditText) view.findViewById(R.id.phoneinput);
        confirmPhone = (Button) view.findViewById(R.id.confirmphone);
        p2 = (ImageView) view.findViewById(R.id.p22);

        emailPanel = (RelativeLayout) view.findViewById(R.id.emailpanel);
        emailPanel2 = (RelativeLayout) view.findViewById(R.id.emailpanel2);
        emailInput = (EditText) view.findViewById(R.id.emailinput);
        confirmEmail = (Button) view.findViewById(R.id.confirmemail);
        p3 = (ImageView) view.findViewById(R.id.p33);

        passwordPanel = (RelativeLayout) view.findViewById(R.id.passwordpanel);
        passwordPanel2 = (RelativeLayout) view.findViewById(R.id.passwordpanel2);
        passwordInput = (EditText) view.findViewById(R.id.passwordinput);
        confirmPassword = (Button) view.findViewById(R.id.confirmpassword);
        p4 = (ImageView) view.findViewById(R.id.p44);

        deleteSwitch = (Switch)view.findViewById(R.id.deleteswitch);



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        mref = FirebaseDatabase.getInstance().getReference().child("users");

        //TODO Profile picture won't work change it to firebase storage

        //Picture
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(Uri.parse("http://www.hit4hit.org/img/login/user-icon-6.png"))
                        .build();
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(getContext(),"Picture updated",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        //Name
        namePanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(namePanel2.getVisibility() == View.GONE){
                    namePanel2.setVisibility(View.VISIBLE);
                    nameInput.setVisibility(View.VISIBLE);
                    confirmName.setVisibility(View.VISIBLE);
                    p1.setImageResource(R.drawable.expandup);
                    confirmName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String newName = nameInput.getText().toString();

                            if((!newName.matches(""))&&(newName.trim().length()>0)){
                                DatabaseReference mRefUser = mref.child(user.getUid());
                                Map<String,Object> nameUpdate = new HashMap<String, Object>();
                                nameUpdate.put("displayName",newName);
                                mRefUser.updateChildren(nameUpdate);
                                Toast.makeText(getContext(),"Your display name is now "+ newName,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    namePanel2.setVisibility(View.GONE);
                    nameInput.setVisibility(View.GONE);
                    confirmName.setVisibility(View.GONE);
                    p1.setImageResource(R.drawable.expanddown);
                }
            }
        });

        //Phone
        phonePanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(phonePanel2.getVisibility() == View.GONE){
                    phonePanel2.setVisibility(View.VISIBLE);
                    phoneInput.setVisibility(View.VISIBLE);
                    confirmPhone.setVisibility(View.VISIBLE);
                    confirmPhone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String newPhone = phoneInput.getText().toString();

                            if(validCellPhone(newPhone)){
                                DatabaseReference mRefUser = mref.child(user.getUid());
                                Map<String,Object> phoneUpdate = new HashMap<String, Object>();
                                phoneUpdate.put("phoneNumber",newPhone);
                                mRefUser.updateChildren(phoneUpdate);
                                Toast.makeText(getContext(),"Your phone number is now "+ newPhone,Toast.LENGTH_SHORT).show();
                            }                        }
                    });
                    p2.setImageResource(R.drawable.expandup);
                }else{
                    phonePanel2.setVisibility(View.GONE);
                    phoneInput.setVisibility(View.GONE);
                    confirmPhone.setVisibility(View.GONE);
                    p2.setImageResource(R.drawable.expanddown);
                }
            }
        });

        //Email
        emailPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(emailPanel2.getVisibility() == View.GONE){
                    emailPanel2.setVisibility(View.VISIBLE);
                    emailInput.setVisibility(View.VISIBLE);
                    confirmEmail.setVisibility(View.VISIBLE);
                    confirmEmail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String email = emailInput.getText().toString();
                            if (validEmail(email)) {

                                //Alert Dialog
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                alertDialog.setTitle("Password");
                                alertDialog.setMessage("Please enter your password to continue");
                                alertDialog.setIcon(R.drawable.key);

                                final EditText input = new EditText(getActivity());
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);
                                input.setLayoutParams(lp);
                                alertDialog.setView(input);

                                alertDialog.setPositiveButton("Confirm",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String password = input.getText().toString();
                                                AuthCredential credential = EmailAuthProvider
                                                        .getCredential(user.getEmail(), password);

                                                user.reauthenticate(credential)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(getContext(), "Success reauth", Toast.LENGTH_SHORT).show();
                                                                user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if(task.isSuccessful()) {
                                                                            Toast.makeText(getContext(), "Email updated", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), "Failure reauth", Toast.LENGTH_SHORT).show();

                                                    }
                                                });
                                            }
                                        });

                                alertDialog.setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        }).create().show();

                                //Alertdialog

                            }else {
                                Toast.makeText(getContext(), "Invalid email address", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                    p3.setImageResource(R.drawable.expandup);
                }else{
                    emailPanel2.setVisibility(View.GONE);
                    emailInput.setVisibility(View.GONE);
                    confirmEmail.setVisibility(View.GONE);
                    p3.setImageResource(R.drawable.expanddown);
                }
            }
        });

        //Password
        passwordPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(passwordPanel2.getVisibility() == View.GONE){
                    passwordPanel2.setVisibility(View.VISIBLE);
                    passwordInput.setVisibility(View.VISIBLE);
                    confirmPassword.setVisibility(View.VISIBLE);
                    confirmPassword.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String newPassword = passwordInput.getText().toString();
                            if (validPassword(newPassword)) {

                                //Alert Dialog
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                alertDialog.setTitle("Password");
                                alertDialog.setMessage("Please enter your old password to continue");
                                alertDialog.setIcon(R.drawable.key);

                                final EditText input = new EditText(getActivity());
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);
                                input.setLayoutParams(lp);
                                alertDialog.setView(input);

                                alertDialog.setPositiveButton("Confirm",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String password = input.getText().toString();
                                                AuthCredential credential = EmailAuthProvider
                                                        .getCredential(user.getEmail(), password);

                                                user.reauthenticate(credential)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(getContext(), "Success reauth", Toast.LENGTH_SHORT).show();
                                                                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if(task.isSuccessful()) {
                                                                            Toast.makeText(getContext(), "Password updated", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), "Failure reauth", Toast.LENGTH_SHORT).show();

                                                    }
                                                });
                                            }
                                        });

                                alertDialog.setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        }).create().show();

                                //Alertdialog

                            }else {
                                Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                    p4.setImageResource(R.drawable.expandup);
                }else{
                    passwordPanel2.setVisibility(View.GONE);
                    passwordInput.setVisibility(View.GONE);
                    confirmPassword.setVisibility(View.GONE);
                    p4.setImageResource(R.drawable.expanddown);
                }
            }
        });

        //Delete
        deleteSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Alert Dialog
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Password");
                alertDialog.setMessage("Please enter your password to delete your account");
                alertDialog.setIcon(R.drawable.key);

                final EditText input = new EditText(getActivity());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String password = input.getText().toString();
                                AuthCredential credential = EmailAuthProvider
                                        .getCredential(user.getEmail(), password);

                                user.reauthenticate(credential)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(getContext(), "Success reauth", Toast.LENGTH_SHORT).show();
                                                mref.child(user.getUid()).removeValue();
                                                user.delete()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    ((MainActivity)getActivity()).signout();
                                                                }
                                                            }
                                                        });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Failure reauth", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        });

                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                deleteSwitch.setChecked(false);
                            }
                        }).create().show();

                //Alertdialog
            }
        });

    }

    public boolean validCellPhone(String number)
    {
        return android.util.Patterns.PHONE.matcher(number).matches();
    }

    public boolean validEmail(String email)
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean validPassword(String password)
    {
        return password.trim().length()>5;
    }
}
