package com.innovativetools.scanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.text.Html;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Objects;

public class Setting extends AppCompatActivity  {

    BottomNavigationView btmnav;

    @SuppressLint("IntentReset")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        btmnavutil();


        findViewById(R.id.privacy).setOnClickListener(view ->
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://bit.ly/3DfaRjI"));
            startActivity(intent);
        });


        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#ffffff\">" + "Setting" + "</font>"));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        if(findViewById(R.id.framelayout) != null){
            if(savedInstanceState != null){
                return;
            }
            getFragmentManager().beginTransaction().
                    replace(R.id.framelayout, new PreferenceFragment()).commit();
        }

        findViewById(R.id.feedback).setOnClickListener(view -> {
            try {

                Intent email = new Intent(Intent.ACTION_SENDTO,Uri
                .fromParts("mailto","innovativetuls@gmail.com",null));
                email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                startActivity(Intent.createChooser(email,null));

            }catch (Exception e){
                e.printStackTrace();
            }

        });



    }


    @SuppressLint("ValidFragment")
    public static class PreferenceFragment extends android.preference.PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
    {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
                PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {


        }
    }




    private void btmnavutil(){

        btmnav = findViewById(R.id.btmnavigation);
        btmnav.setSelectedItemId(R.id.setting);
        btmnav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.setting:
                        return true;

                    case R.id.scanner:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.createQR:
                        startActivity(new Intent(getApplicationContext(), Generate.class));
                        overridePendingTransition(0, 0);
                        return true;

                }
                return true;
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,ScannerActivity.class));
        finish();
    }

}