package com.innovativetools.scanner.createcodes;

import static com.innovativetools.scanner.Constants.WRITE_EXTERNAL_STORAGE_REQUEST;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.zxing.BarcodeFormat;
import com.innovativetools.scanner.Constants;
import com.innovativetools.scanner.Generate;
import com.innovativetools.scanner.R;
import com.innovativetools.scanner.generatecodes.Contents;
import com.innovativetools.scanner.utils.QRGeneratorUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class Email extends AppCompatActivity {


    Bitmap bitmap;
    EditText emailtxt;
    EditText subject;
    EditText message;
    ImageView outputqr;
    String result;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#ffffff\">" + "E-mail" + "</font>"));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        emailtxt  = findViewById(R.id.emailtext);
        subject = findViewById(R.id.subject);
        message = findViewById(R.id.message);
        outputqr = findViewById(R.id.generated_qr);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.confirm,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.cnfrm) {

            result = emailtxt.getText().toString();
            if (result.isEmpty()) {
                Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
            } else {

                boolean subjectAdded = false;
                if (!subject.getText().toString().isEmpty()) {
                    result += "?" + "subject=" + Uri.encode(subject.getText().toString());
                    subjectAdded = true;
                }
                if (!message.getText().toString().isEmpty()) {
                    result += (subjectAdded ? "&" : "?") + "body=" + Uri.encode(message.getText().toString());
                }

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(emailtxt.getApplicationWindowToken(), 0);

                outputqr.setVisibility(View.VISIBLE);
                findViewById(R.id.textInputLayout).setVisibility(View.GONE);
                findViewById(R.id.outputlayout).setVisibility(View.VISIBLE);

                try {
                    Glide.with(this).asBitmap().load(QRGeneratorUtils.createImage(this, result, Contents.Type.EMAIL, BarcodeFormat.QR_CODE, Constants.errorCorrectionsQR[0])).into(new BitmapImageViewTarget(outputqr));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, Generate.class));
    }
}