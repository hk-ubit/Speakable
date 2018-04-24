package com.hkubit.thespeakable;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class SettingsActivity extends AppCompatActivity {
    private DatabaseReference mref;
    private FirebaseUser muser;
    private TextView mStatus;
    private TextView mname;
    private Button mstatbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mname = (TextView) findViewById(R.id.settings_name);
        mStatus = (TextView) findViewById(R.id.settings_status);
        mstatbtn = (Button) findViewById(R.id.settings_change_status);
        mstatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent statusint = new Intent(SettingsActivity.this,StatusActivity.class);
                startActivity(statusint);
            }
        });


        muser = FirebaseAuth.getInstance().getCurrentUser();
        String userid = muser.getUid();
        mref = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("settings activity",dataSnapshot.toString());
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String img = dataSnapshot.child("img").getValue().toString();
                String thumb = dataSnapshot.child("thumbimg").getValue().toString();
                mname.setText(name);
                mStatus.setText(status);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
