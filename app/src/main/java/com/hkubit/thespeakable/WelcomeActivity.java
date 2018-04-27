package com.hkubit.thespeakable;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

    private Button mcreateBtn;
    private Button mloginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mcreateBtn = (Button) findViewById(R.id.welcome_create_account);
        mloginBtn = (Button) findViewById(R.id.welcome_login_account);
        mcreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg_intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
                startActivity(reg_intent);
            }
        });
        mloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent log_intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(log_intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
