package com.kallinikos.tech.sweetdealsfire.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kallinikos.tech.sweetdealsfire.dbmodels.User;
import com.kallinikos.tech.sweetdealsfire.R;

public class SignUp extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    private EditText usernameText;
    private EditText mobileText;
    private EditText emailText;
    private EditText passwordText;
    private EditText confirmText;


    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //prevent keyboard from popping out
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        emailText = (EditText)findViewById(R.id.input_email);
        passwordText = (EditText)findViewById(R.id.input_password);
        mobileText = (EditText)findViewById(R.id.input_mobile);
        usernameText = (EditText)findViewById(R.id.input_username);
        confirmText = (EditText)findViewById(R.id.input_confirm_password);

        progressDialog = new ProgressDialog(this);

        findViewById(R.id.btn_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                if (id == R.id.btn_signup){
                    signUp(emailText.getText().toString(),passwordText.getText().toString());
                }
            }
        });

        findViewById(R.id.link_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //-----FirebaseAuth-----
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null){
                    //User is signed in
                    Log.d(TAG,"onAuthStateChanged:signed_in:"+user.getUid());
                }else {
                    //User is signed out
                    Log.d(TAG,"onAuthStateChanged:signed_out:");
                }
            }
        };
        //-----FirebaseAuth-----

        //-----FirebaseRD-----
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //-----FirebaseRD-----


    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void signUp(String email,String password){
        Log.d(TAG,"signUp"+email);
        if(!validate()){
            return;
        }

        // showProgressDialog();
        progressDialog.setMessage("Creating account...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.v(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            progressDialog.dismiss();
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(SignUp.this, "User with this email already exists.", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(SignUp.this,"Sign up Failed",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            //send verification email -----rectify-----

                            //User id
                            String Uid = mAuth.getCurrentUser().getUid().toString();
                            //Store User object to db
                            User user = new User(usernameText.getText().toString(),mobileText.getText().toString());
                            mDatabase.child("users").child(Uid).setValue(user);

                            Toast.makeText(SignUp.this,"Welcome "+user.displayName.toString()+"!!",Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(i);
                            finish();
                            progressDialog.dismiss();
                        }

                    }
                });

    }

    public boolean validate() {
        boolean valid = true;

        String username = usernameText.getText().toString();
        String mobile = mobileText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String confirm = confirmText.getText().toString();
/*
        if (realname.isEmpty() || realname.length() < 3) {
            realnameText.setError("at least 3 characters");
            valid = false;
        } else {
            realnameText.setError(null);
        }*/

        if (username.isEmpty() || username.length() < 3) {
            usernameText.setError("at least 3 characters");
            valid = false;
        } else {
            usernameText.setError(null);
        }

        if (mobile.isEmpty() || mobile.length() < 8) {
            mobileText.setError("at least 8 digits");
            valid = false;
        } else {
            mobileText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 20) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        if (confirm.isEmpty() || !confirm.equals(password)){
            confirmText.setError("must be equal to password");
            valid = false;
        }else{
            confirmText.setError(null);
        }

        return valid;
    }
}
