package com.hkubit.thespeakable;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ViewPager mViewPager;
    private SectionAdapter madapter;
    private TabLayout mtabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        Toolbar myToolbar = (Toolbar) findViewById(R.id.mainactivity_toolbar);
        setSupportActionBar(myToolbar);
        mViewPager = (ViewPager) findViewById(R.id.mainactivity_view_pager);
        madapter = new SectionAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(madapter);
        mtabLayout = (TabLayout) findViewById(R.id.mainactivity_tab_layout);
        mtabLayout.setTabTextColors(
                ContextCompat.getColor(MainActivity.this, R.color.textPrimary),
                ContextCompat.getColor(MainActivity.this, R.color.text_icons)
        );
        mtabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_appbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout: {
                FirebaseAuth.getInstance().signOut();
                updateUi(null);

                return true;
            }
            case R.id.action_help:
                Intent settingIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingIntent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = checkSignIn();
        if (currentUser == null) {
            updateUi(null);
            finish();
        }

    }

    private FirebaseUser checkSignIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser;
    }

    private void updateUi(FirebaseUser user) {
        if (user == null) {
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
