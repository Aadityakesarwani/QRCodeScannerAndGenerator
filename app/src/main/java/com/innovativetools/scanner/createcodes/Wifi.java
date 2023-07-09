package com.innovativetools.scanner.createcodes;

import static com.innovativetools.scanner.Constants.WRITE_EXTERNAL_STORAGE_REQUEST;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.zxing.BarcodeFormat;
import com.innovativetools.scanner.Constants;
import com.innovativetools.scanner.R;
import com.innovativetools.scanner.generatecodes.Contents;
import com.innovativetools.scanner.utils.QRGeneratorUtils;

import java.io.IOException;
import java.util.Objects;

public class Wifi extends AppCompatActivity {

    private String[] auth;
    private ArrayAdapter<String> dropdownAdapter;
    private AutoCompleteTextView dropdownMenu;

    EditText qrNetwork;
    EditText qrPassword;
    ImageView outputqr;

    Bitmap bitmap;

    String result = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);


        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#ffffff\">" + "Wifi" + "</font>"));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        auth = new String[]{getString(R.string.no_encryption), "WEP", "WPA/WPA2"};

        dropdownMenu = findViewById(R.id.editWifiEncryption);
        dropdownAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, auth);
        dropdownAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        dropdownMenu.setAdapter(dropdownAdapter);
        dropdownMenu.setText(auth[2], false);
        dropdownMenu.setAdapter(dropdownAdapter);

        qrNetwork = findViewById(R.id.networkname);
        qrPassword = findViewById(R.id.password);

        dropdownMenu.setOnItemClickListener((adapterView, view, i, l) -> {
            if (dropdownMenu.getText().toString().equals(auth[0])) {
                //disable password field if no encryption was selected
                findViewById(R.id.passwordlayout).setEnabled(false);
            } else {
                findViewById(R.id.passwordlayout).setEnabled(true);
            }
        });



        outputqr  = findViewById(R.id.generated_qr);
        ImageView save  = findViewById(R.id.save_generated_qr);
        ImageView share  = findViewById(R.id.share_generated_qr);



        save.setOnClickListener(view -> {
            bitmap = ((BitmapDrawable)outputqr.getDrawable()).getBitmap();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        QRGeneratorUtils.saveImageToExternalStorage(this,bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST);
                }
            }
            else
            {
                try {
                    QRGeneratorUtils.saveImageToExternalStorage(this,bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }



        });

        share.setOnClickListener(view -> {
            QRGeneratorUtils.shareImage(this,QRGeneratorUtils.getCachedUri());
        });

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.confirm,menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cnfrm) {

            try{
            //  WIFI:S:mynetwork;T:WPA;P:mypass;;
            result += "S:" + QRGeneratorUtils.escapeQRPropertyValue(qrNetwork.getText().toString());

            // Add encryption type if encryption was selected
            if (!dropdownMenu.getText().toString().equals(auth[0])) {
                result += ";T:";
                if (dropdownMenu.getText().toString().equals(auth[1])) {
                    result += "WEP";
                } else if (dropdownMenu.getText().toString().equals(auth[2])) {
                    result += "WPA";
                }

                // Add password
                if (!qrPassword.getText().toString().isEmpty()) {
                    result += ";P:" + QRGeneratorUtils.escapeQRPropertyValue(qrPassword.getText().toString());
                }
            }
            result += ";;";


            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(qrNetwork.getApplicationWindowToken(), 0);
            inputMethodManager.hideSoftInputFromWindow(qrPassword.getApplicationWindowToken(), 0);


            findViewById(R.id.inputlayout).setVisibility(View.GONE);
            findViewById(R.id.outputlayout).setVisibility(View.VISIBLE);
             outputqr.setVisibility(View.VISIBLE);

            try {
                Glide.with(this).asBitmap().load(QRGeneratorUtils.createImage(this, result, Contents.Type.WIFI, BarcodeFormat.QR_CODE, Constants.errorCorrectionsQR[0])).into(new BitmapImageViewTarget(outputqr));
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }catch(Exception e) {
                e.printStackTrace();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    QRGeneratorUtils.saveImageToExternalStorage(this,bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "storage permission denied", Toast.LENGTH_LONG).show();
            }
        }

    }

}