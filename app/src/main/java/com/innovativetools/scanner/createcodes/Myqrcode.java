


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

public class Myqrcode extends AppCompatActivity {


    EditText fullname;
    EditText phone;
    EditText address;
    EditText email;
    EditText about;

    Bitmap bitmap;


    ImageView outputqr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myqrcode);

        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#ffffff\">" + "MyInfo" + "</font>"));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);


        fullname  = findViewById(R.id.fullname);
        phone = findViewById(R.id.phone_number);
        address = findViewById(R.id.myaddress);
        email = findViewById(R.id.myqr_email);
        about = findViewById(R.id.aboutme);


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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.confirm,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.cnfrm){

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(fullname.getWindowToken(),0);
            inputMethodManager.hideSoftInputFromWindow(phone.getWindowToken(),0);
            inputMethodManager.hideSoftInputFromWindow(email.getWindowToken(),0);
            inputMethodManager.hideSoftInputFromWindow(about.getWindowToken(),0);
            inputMethodManager.hideSoftInputFromWindow(address.getWindowToken(),0);


            String result  = "N:"+fullname.getText().toString()+"\n"
                    +  "ORG:" + about.getText().toString()+"\n"
                    + "ADR:;;" + address.getText().toString()+"\n"
                    + "EMAIL;WORK;INTERNET:" + email.getText().toString()+"\n"
                    +  "TEL;CELL:" + phone.getText().toString()+"\n"
                    +"END:VCARD";

            outputqr.setVisibility(View.VISIBLE);
            findViewById(R.id.inputlayout).setVisibility(View.GONE);
            findViewById(R.id.outputlayout).setVisibility(View.VISIBLE);

            try{
                Glide.with(this).asBitmap().load(QRGeneratorUtils.createImage(this, result, Contents.Type.V_CARD, BarcodeFormat.QR_CODE , Constants.errorCorrectionsQR[0])).into(new BitmapImageViewTarget(outputqr));
            }catch (Exception e){
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