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
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.innovativetools.scanner.Constants;
import com.innovativetools.scanner.R;
import com.innovativetools.scanner.generatecodes.Contents;
import com.innovativetools.scanner.utils.QRGeneratorUtils;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;
import java.util.Objects;

public class Sms extends AppCompatActivity  {


    RelativeLayout layout;
    LinearLayout linearLayout;
    EditText phone,msg;
    ImageView ouptut;


    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#ffffff\">" + "SMS" + "</font>"));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        phone = findViewById(R.id.phonetext);
        msg = findViewById(R.id.msgText);
        layout = findViewById(R.id.outputlayout);
        linearLayout  = findViewById(R.id.edittextlayout);
        ouptut = findViewById(R.id.generated_qr);


        findViewById(R.id.save_generated_qr).setOnClickListener(view -> {
            bitmap = ((BitmapDrawable)ouptut.getDrawable()).getBitmap();
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

        findViewById(R.id.share_generated_qr).setOnClickListener(view -> {
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
            inputMethodManager.hideSoftInputFromWindow(phone.getApplicationWindowToken(),0);
            inputMethodManager.hideSoftInputFromWindow(msg.getApplicationWindowToken(),0);



            layout.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);

            try{
            Glide.with(this).asBitmap().load(QRGeneratorUtils.createImage(Sms.this,phone.getText().toString()+":content"+msg.getText().toString(),Contents.Type.SMS,BarcodeFormat.QR_CODE , Constants.errorCorrectionsQR[0])).into(new BitmapImageViewTarget(ouptut));
            }catch (Exception e){
                e.printStackTrace();
             }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QRGeneratorUtils.purgeCacheFolder(this);
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