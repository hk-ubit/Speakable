package com.hkubit.thespeakable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.support.v7.widget.Toolbar;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout nameinp;
    private TextInputLayout emailinp;
    private EditText passinp;
    private Button createBtn;
    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;
    private ProgressBar mprogbar;
    private DatabaseReference mref;
    private static final String TAG = "RegisterActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth=FirebaseAuth.getInstance();
        mtoolbar = (Toolbar) findViewById(R.id.reg_toolbar);
        mprogbar = (ProgressBar)findViewById(R.id.reg_progress);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nameinp = (TextInputLayout) findViewById(R.id.reg_name);
        emailinp = (TextInputLayout) findViewById(R.id.reg_email);
        passinp = (EditText) findViewById(R.id.reg_pass);
        createBtn = (Button) findViewById(R.id.crtBtn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameinp.getEditText().getText().toString();
                String email = emailinp.getEditText().getText().toString();
                String pass = passinp.getText().toString();
              if(validateForm(email,pass))
              {
                 createUser(email,pass,name);
              }
            }
        });
    }

    private boolean validateForm(String email,String password) {
        boolean valid = true;


        if (TextUtils.isEmpty(email)) {
           emailinp.setError("Required.");
            valid = false;
        } else {
            emailinp.setError(null);
        }


        if (TextUtils.isEmpty(password)) {
            passinp.setError("Required.");
            valid = false;
        } else {
            passinp.setError(null);
        }

        return valid;
    }
    public void createUser(String email,String password,String username)
    {mprogbar.setVisibility(View.VISIBLE);
    final String name = username;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                           final FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();
                            mref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                            HashMap<String,String> map = new HashMap<>();
                            map.put("name",name);
                            map.put("status","The Speakable <3 ");
                            map.put("img","default");
                            map.put("thumbimg","default thumb");
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
                            updateUi(null);

                        }

                        // ...
                    }
                });
    }
    private void updateUi(FirebaseUser user){
        if (user == null)
        {
            Intent intent = new Intent(RegisterActivity.this,WelcomeActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
            startActivity(intent);
        }
    }
}
