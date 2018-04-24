package com.hkubit.thespeakable;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private DatabaseReference mref;
    private FirebaseUser muser;
    private Button mupdbtn;
    private TextInputLayout mstatusinp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mtoolbar = (Toolbar)findViewById(R.id.status_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        muser = FirebaseAuth.getInstance().getCurrentUser();
        String userid = muser.getUid();
        mref = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        mupdbtn = (Button) findViewById(R.id.status_update_btn);
        mstatusinp = (TextInputLayout) findViewById(R.id.update_status_view);
        mupdbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mref.child("status").setValue(mstatusinp.getEditText().getText().toString());
                finish();
            }
        });

    }
}
