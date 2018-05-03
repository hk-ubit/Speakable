package com.hkubit.thespeakable;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {
    ImageView mprofileImage;
    TextView mNameView, mStatusView;
    Button mSendreqBtn, mDeclineReqBtn;
    DatabaseReference mDbref,mReqref,mFriendsref;
    String mprofname;
    FirebaseUser mcurrentuser;
    String mcurrentstate = "not_friends";
    RelativeLayout mprofilePage;
    ProgressBar mprogbar;
    Button mRejectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String profid = getIntent().getStringExtra("id");
        mprofileImage = findViewById(R.id.profile_profile_image);
        mNameView = findViewById(R.id.profile_profile_name);
        mStatusView = findViewById(R.id.profile_profie_status);
        mSendreqBtn = findViewById(R.id.profile_send_req);
        mDeclineReqBtn = findViewById(R.id.profile_decline_req);
        mcurrentuser = FirebaseAuth.getInstance().getCurrentUser();
        mDbref = FirebaseDatabase.getInstance().getReference().child("Users").child(profid);
        mReqref = FirebaseDatabase.getInstance().getReference().child("friend_requests");
        mFriendsref = FirebaseDatabase.getInstance().getReference().child("friends");
        mprofilePage = findViewById(R.id.profilepage);
        mprogbar = findViewById(R.id.profile_progress_bar);
        mRejectRequest = findViewById(R.id.profile_decline_req);
        mprogbar.bringToFront();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mprogbar.setVisibility(View.VISIBLE);



        mDbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("dhahd", dataSnapshot.toString());
                mprofname = dataSnapshot.child("name").getValue().toString();
                String img = dataSnapshot.child("img").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                mNameView.setText(mprofname);
                mStatusView.setText(status);
                Picasso.get().load(img).placeholder(R.drawable.profile).into(mprofileImage);

                mReqref.child(mcurrentuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(profid))
                        {
                            String req_type = dataSnapshot.child(profid).child("request_type").getValue().toString();
                            Toast.makeText(ProfileActivity.this, req_type, Toast.LENGTH_SHORT).show();
                            if(req_type.equals("sent"))
                            {
                                mcurrentstate="req_sent";
                                mSendreqBtn.setText("Cancel Friend Request");
                            }
                            else if (req_type.equals("received"))
                            {
                                mcurrentstate="req_rec";
                                mSendreqBtn.setText("Accept Friend Request");
                                mRejectRequest.setVisibility(View.VISIBLE);
                            }

                        }
                        else {
                            mFriendsref.child(mcurrentuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(profid))
                                    {
                                        mcurrentstate="friends";
                                        mSendreqBtn.setText("Unfriend "+mprofname);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    mprogbar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mSendreqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //when BOTH USERS ARE NOT CONNECTED
                mSendreqBtn.setEnabled(false);
                if(mcurrentstate.equals("not_friends")) {

                    mReqref.child(mcurrentuser.getUid()).child(profid).child("request_type").setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mReqref.child(profid).child(mcurrentuser.getUid()).child("request_type").setValue("received")
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(ProfileActivity.this, "Request sent succesfull", Toast.LENGTH_SHORT).show();
                                                            mSendreqBtn.setEnabled(true);
                                                            mcurrentstate="req_sent";
                                                            mSendreqBtn.setText("Cancel Friend Request");
                                                        } else {
                                                            Toast.makeText(ProfileActivity.this, "Failed to send friend request", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(ProfileActivity.this, "Failed to sent friend request", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }



                // when CURRENT USER HAS SENT REQUEST TO THE GIVEN PROFILE USER
               else if (mcurrentstate.equals("req_sent"))
                {
                    mReqref.child(mcurrentuser.getUid()).child(profid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mReqref.child(profid).child(mcurrentuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ProfileActivity.this, "Cancelled Friend Request", Toast.LENGTH_SHORT).show();
                                    mcurrentstate="not_friends";
                                    mSendreqBtn.setText("Send Request");
                                    mSendreqBtn.setEnabled(true);
                                }
                            });
                        }
                    });
                }

                // when given profile has sent request to us
                else if (mcurrentstate.equals("req_rec"))
                {
                    final String date = DateFormat.getDateTimeInstance().format(new Date());
                    mFriendsref.child(mcurrentuser.getUid()).child(profid).setValue(date).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                mFriendsref.child(profid).child(mcurrentuser.getUid()).setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mReqref.child(profid).child(mcurrentuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mReqref.child(mcurrentuser.getUid()).child(profid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful())
                                                        {
                                                            mSendreqBtn.setEnabled(true);
                                                            mSendreqBtn.setText("Unfriend "+mprofname);
                                                            mcurrentstate="friends";
                                                        }
                                                        else {
                                                            Toast.makeText(ProfileActivity.this, "Couldn't accept friend request", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }

                        }

                    });


                }

                //when both are friends already
                else if(mcurrentstate.equals("friends"))
                {
                    mFriendsref.child(mcurrentuser.getUid()).child(profid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mFriendsref.child(profid).child(mcurrentuser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mSendreqBtn.setText("Send Request");
                                    mcurrentstate="not_friends";
                                    mSendreqBtn.setEnabled(true);
                                }
                            });
                        }
                    });
                }
            }
        });
        mRejectRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReqref.child(mcurrentuser.getUid()).child(profid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mReqref.child(profid).child(mcurrentuser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    mRejectRequest.setVisibility(View.GONE);
                                    mSendreqBtn.setText("Send Request");
                                    mcurrentstate="not_friends";
                                }
                            }
                        });
                    }
                });
            }
        });





    }
}
