package com.hkubit.thespeakable;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private DatabaseReference mDBref;
    private FirebaseUser mCurrentUser;
    private Button mUpdateBtn;
    private TextInputLayout mStatusField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mtoolbar = (Toolbar) findViewById(R.id.status_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = mCurrentUser.getUid();
        mDBref = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        mUpdateBtn = (Button) findViewById(R.id.status_update_btn);
        mStatusField = (TextInputLayout) findViewById(R.id.status_update_field);
        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDBref.child("status").setValue(mStatusField.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(StatusActivity.this, "Status Updated Succesfully", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
            }
        });

    }
}
