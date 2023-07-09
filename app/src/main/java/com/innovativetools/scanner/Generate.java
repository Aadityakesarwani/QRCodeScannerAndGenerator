package com.innovativetools.scanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.zxing.pdf417.encoder.PDF417;
import com.innovativetools.scanner.createcodes.Email;
import com.innovativetools.scanner.createcodes.Location;
import com.innovativetools.scanner.createcodes.Myqrcode;
import com.innovativetools.scanner.createcodes.Phone;
import com.innovativetools.scanner.createcodes.others.Aztech;
import com.innovativetools.scanner.createcodes.others.Code128;
import com.innovativetools.scanner.createcodes.others.Code39;
import com.innovativetools.scanner.createcodes.others.Code93;
import com.innovativetools.scanner.createcodes.others.Codebar;
import com.innovativetools.scanner.createcodes.others.Datamatrix;
import com.innovativetools.scanner.createcodes.others.Ean13;
import com.innovativetools.scanner.createcodes.others.Ean8;
import com.innovativetools.scanner.createcodes.others.Itf;
import com.innovativetools.scanner.createcodes.others.Pdf417;
import com.innovativetools.scanner.createcodes.others.Upca;
import com.innovativetools.scanner.createcodes.Sms;
import com.innovativetools.scanner.createcodes.Text;
import com.innovativetools.scanner.createcodes.Url;
import com.innovativetools.scanner.createcodes.Wifi;
import com.innovativetools.scanner.createcodes.others.Upce;

import java.util.ArrayList;
import java.util.Objects;

public class Generate extends AppCompatActivity {

    BottomNavigationView btmnav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);

        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#ffffff\">" + "Generate" + "</font>"));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);


        btmnavview();

        findViewById(R.id.text).setOnClickListener(view -> {
            startActivity(new Intent(Generate.this, Text.class));
        });

        findViewById(R.id.sms).setOnClickListener(view -> {
            startActivity(new Intent(Generate.this, Sms.class));
        });

        findViewById(R.id.phone).setOnClickListener(view -> {
            startActivity(new Intent(Generate.this, Phone.class));

        });

        findViewById(R.id.phone).setOnClickListener(view -> {
            startActivity(new Intent(Generate.this, Phone.class));
        });

        findViewById(R.id.location).setOnClickListener(view -> {
            startActivity(new Intent(Generate.this, Location.class));

        });

        findViewById(R.id.url).setOnClickListener(view -> {
            startActivity(new Intent(Generate.this, Url.class));

        });

        findViewById(R.id.email).setOnClickListener(view -> {
            startActivity(new Intent(Generate.this, Email.class));

        });

        findViewById(R.id.contact).setOnClickListener(view -> {
            startActivity(new Intent(Generate.this, Myqrcode.class));

        });

        findViewById(R.id.wifi).setOnClickListener(view -> {
            startActivity(new Intent(Generate.this, Wifi.class));

        });


        findViewById(R.id.upca).setOnClickListener(view -> {
            Intent intent  = new Intent(Generate.this, Upca.class);
            startActivity(intent);

        });

        findViewById(R.id.upce).setOnClickListener(view -> {
            Intent intent  = new Intent(Generate.this, Upce.class);
            startActivity(intent);
            });

      findViewById(R.id.code39).setOnClickListener(view -> {
          startActivity(new Intent(this, Code39.class));

      });

        findViewById(R.id.code93).setOnClickListener(view -> {
            startActivity(new Intent(this, Code93.class));
        });


        findViewById(R.id.aztech).setOnClickListener(view -> {
            startActivity(new Intent(this, Aztech.class));
        });
//
//
        findViewById(R.id.code128).setOnClickListener(view -> {
            startActivity(new Intent(this, Code128.class));
        });


        findViewById(R.id.codebar).setOnClickListener(view -> {
            startActivity(new Intent(this, Codebar.class));
        });


        findViewById(R.id.datamatrix).setOnClickListener(view -> {
            startActivity(new Intent(this, Datamatrix.class));
        });


        findViewById(R.id.ean8).setOnClickListener(view -> {
            startActivity(new Intent(this, Ean8.class));
        });


        findViewById(R.id.ean13).setOnClickListener(view -> {
            startActivity(new Intent(this, Ean13.class));
        });


        findViewById(R.id.itf).setOnClickListener(view -> {
            startActivity(new Intent(this, Itf.class));
        });


        findViewById(R.id.pdf417).setOnClickListener(view -> {
            startActivity(new Intent(this, Pdf417.class));
        });


    }



    private void btmnavview(){
        btmnav = findViewById(R.id.btmnavigation);
        btmnav.setSelectedItemId(R.id.createQR);


        btmnav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.setting:
                        startActivity(new Intent(getApplicationContext(),Setting.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.scanner:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.createQR:
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