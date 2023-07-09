package com.innovativetools.scanner.createcodes;

import static com.innovativetools.scanner.Constants.WRITE_EXTERNAL_STORAGE_REQUEST;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.util.Log;
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

public class Phone extends AppCompatActivity {


    Bitmap bitmap;

    Intent data;
    ActivityResultLauncher<Intent> launcher =  registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
            , new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode()  == RESULT_OK){
                        data = result.getData();
                        onImportcontact();
                    }

                }
            });



    EditText editphone ;
    ImageView outputqr;



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#ffffff\">" + "Phone" + "</font>"));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);


        editphone =  findViewById(R.id.editphone);
        findViewById(R.id.importcntct).setOnClickListener(view -> importcontact());

        outputqr = findViewById(R.id.generated_qr);
        ImageView save  = findViewById(R.id.save_generated_qr);
        ImageView share  = findViewById(R.id.share_generated_qr);



//
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

            if(editphone.getText().toString().isEmpty())
            {
                Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
            }
            else {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(editphone.getApplicationWindowToken(), 0);

                outputqr.setVisibility(View.VISIBLE);
                findViewById(R.id.inputlayout).setVisibility(View.GONE);
                findViewById(R.id.outputlayout).setVisibility(View.VISIBLE);

                try {
                    Glide.with(this).asBitmap().load(QRGeneratorUtils.createImage(this, editphone.getText().toString(), Contents.Type.PHONE, BarcodeFormat.QR_CODE, Constants.errorCorrectionsQR[0])).into(new BitmapImageViewTarget(outputqr));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return super.onOptionsItemSelected(item);
    }







    @SuppressLint("QueryPermissionsNeeded")
    private void importcontact(){
        try {

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            launcher.launch(intent);

        }catch (Exception e){
            e.printStackTrace();
            Log.d("error",e.getMessage());
        }

    }

    private void onImportcontact(){
        Uri contactUri = data.getData();
        String[] projection =
                {
                        ContactsContract.CommonDataKinds.Phone._ID,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.LABEL
                };
        try {
            Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
            if (cursor.moveToFirst()) {
                int phoneNoIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String phoneNo = cursor.getString(phoneNoIdx);
               editphone.setText(phoneNo);
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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