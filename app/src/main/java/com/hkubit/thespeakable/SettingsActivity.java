package com.hkubit.thespeakable;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SettingsActivity extends AppCompatActivity {
    private DatabaseReference mDbref;
    private FirebaseUser mCurrentUser;
    private TextView mStatusView;
    private TextView mNameView;
    private ImageView mDpImageView;
    private Button mUpdateStatusBtn;
    private Button mUpdateDpBtn;
    private StorageReference mStorageRef;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mToolbar = (Toolbar)findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mNameView = (TextView) findViewById(R.id.settings_name_view);
        mStatusView = (TextView) findViewById(R.id.settings_status_view);
        mUpdateStatusBtn = (Button) findViewById(R.id.settings_change_status);
        mDpImageView = (ImageView) findViewById(R.id.settings_profile_image);
        mUpdateDpBtn = (Button) findViewById(R.id.settings_change_pic);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mUpdateStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent statusint = new Intent(SettingsActivity.this, StatusActivity.class);
                startActivity(statusint);
            }
        });

        mUpdateDpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);
            }
        });


        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userid = mCurrentUser.getUid();
        mDbref = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        mDbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("settings activity", dataSnapshot.toString());
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String img = dataSnapshot.child("img").getValue().toString();
                String thumb = dataSnapshot.child("thumbimg").getValue().toString();
                mNameView.setText(name);
                mStatusView.setText(status);
                Picasso.get().load(img).into(mDpImageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                StorageReference secref = mStorageRef.child("profile_pictures").child(mCurrentUser.getUid() + ".jpg");
                secref.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            String dpdownloadurl = task.getResult().getDownloadUrl().toString();
                            mDbref.child("img").setValue(dpdownloadurl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(SettingsActivity.this, "Profile Picture Updated", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else
                            Toast.makeText(SettingsActivity.this, "Failed to add picture in db", Toast.LENGTH_SHORT).show();
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
