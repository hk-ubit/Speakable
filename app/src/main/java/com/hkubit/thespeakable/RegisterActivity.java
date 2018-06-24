package com.hkubit.thespeakable;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private TextInputLayout nameField;
    private TextInputLayout emailField;
    private TextInputLayout passwordField;
    private Button createBtn;
    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;
    private ProgressBar mprogbar;
    private DatabaseReference mref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        mtoolbar = (Toolbar) findViewById(R.id.reg_toolbar);
        mprogbar = (ProgressBar) findViewById(R.id.reg_progress);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nameField = (TextInputLayout) findViewById(R.id.reg_name);
        emailField = (TextInputLayout) findViewById(R.id.reg_email);
        passwordField = (TextInputLayout) findViewById(R.id.reg_pass);
        createBtn = (Button) findViewById(R.id.reg_createbtn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameField.getEditText().getText().toString();
                String email = emailField.getEditText().getText().toString();
                String pass = passwordField.getEditText().getText().toString();
                if (validateForm(email, pass)) {
                    createUser(email, pass, name);
                }
            }
        });
    }

    private boolean validateForm(String email, String password) {
        boolean valid = true;


        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required.");
            valid = false;
        } else {
            emailField.setError(null);
        }


        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Required.");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        return valid;
    }

    public void createUser(String email, String password, String username) {
        mprogbar.setVisibility(View.VISIBLE);
        final String name = username;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            String user_token = FirebaseInstanceId.getInstance().getToken();
                            String uid = user.getUid();
                            mref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                            HashMap<String, String> map = new HashMap<>();
                            map.put("name", name);
                            map.put("device_token",user_token);
                            map.put("status", "The TheSpeakable <3 ");
                            map.put("img", "http://s3.amazonaws.com/37assets/svn/765-default-avatar.png");
                            map.put("thumbimg", "default thumb");
                            mref.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mprogbar.setVisibility(View.GONE);
                                    updateUi(user);
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();


                        }

                        // ...
                    }
                });
    }

    private void updateUi(FirebaseUser user) {
        if (user == null) {
            Intent intent = new Intent(RegisterActivity.this, WelcomeActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
