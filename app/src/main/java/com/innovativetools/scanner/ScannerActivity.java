package com.innovativetools.scanner;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.google.zxing.client.android.Intents;
import com.google.zxing.common.HybridBinarizer;
import com.innovativetools.scanner.viewmodels.Resultview;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.journeyapps.barcodescanner.ViewfinderView;
import com.journeyapps.barcodescanner.camera.CameraSettings;

import java.io.InputStream;
import java.util.List;


public class ScannerActivity extends AppCompatActivity {

    private static final int WRITE_EXTERNAL_STORAGE_REQUEST = 0;
    private static final int PERMISSION_CAMERA_REQUEST = 10;
    ScanOptions options;
    BeepManager manager;

    SharedPreferences preferences;

    final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        finish();
                    }
                    else
                    if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d("MainActivity", "Canc elled scan due to missing camera permission");
                        Toast.makeText(ScannerActivity.this, "Camera permission is required", Toast.LENGTH_LONG).show();
                    }
                }
            });



    private DecoratedBarcodeView barcodeScannerView;
    private CaptureManager capture;

    private ImageView pickimage;
    BottomNavigationView btmnav;


    ActivityResultLauncher<Intent> imagpicker =  registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {



            if(result != null && result.getResultCode() == RESULT_OK){

                try{
                    assert result.getData() != null;
                    final Uri imageuri = result.getData().getData();
                    InputStream inputStream = getContentResolver().openInputStream(imageuri);

                    Bitmap bMap = BitmapFactory.decodeStream(inputStream);
                    String contents = null;

                    int[] intArray = new int[bMap.getWidth()*bMap.getHeight()];

                    bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

                    LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                    Reader reader = new MultiFormatReader();
                    Result resu = reader.decode(bitmap);
                    contents = resu.getText();

                    Picked_img_result.pickedimgresult(ScannerActivity.this,resu);

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(ScannerActivity.this, "wrong image", Toast.LENGTH_SHORT).show();
                }

            }else {
                if (result.getResultCode() == 17) {
                    finish();
                }
            }


        }
    });





final private BarcodeCallback callback = new BarcodeCallback() {
    @Override
    public void barcodeResult(BarcodeResult result) {
        onBarcodresult(result);
    }

    @Override
    public void possibleResultPoints(List<ResultPoint> resultPoints) {
        BarcodeCallback.super.possibleResultPoints(resultPoints);
    }

};

    private void onBarcodresult(BarcodeResult result){
        String contents = result.toString();
        if (contents.isEmpty()) {
            return;
        }

        if (preferences.getBoolean("enablebeep", false)) {
            manager.setBeepEnabled(true);
            manager.playBeepSoundAndVibrate();
        }
        Results.startResultActivity(this,result);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try { setContentView(R.layout.activity_scanner);

            getSupportActionBar().hide();

            manager = new BeepManager(this);

            preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());


        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        capture = new CaptureManager(this, barcodeScannerView);
        capture.decode();



        btmnav = findViewById(R.id.btmnavigation);
        btmnav.setSelectedItemId(R.id.scanner);
        btmnav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.setting:
                        startActivity(new Intent(getApplicationContext(),Setting.class));
                        overridePendingTransition(0,0);
                        return false;

                    case R.id.scanner:
                        return true;

                    case R.id.createQR:
                        startActivity(new Intent(getApplicationContext(),Generate.class));
                        overridePendingTransition(0,0);
                        return false;
                }
                return true;
            }
        });



        ImageView off = findViewById(R.id.off_flash);
        ImageView on = findViewById(R.id.on_flash) ;





            off.setOnClickListener(view -> {
                barcodeScannerView.setTorchOn();
                off.setVisibility(View.GONE);
                on.setVisibility(View.VISIBLE);
            });

            on.setOnClickListener(view -> {
                barcodeScannerView.setTorchOff();
                off.setVisibility(View.VISIBLE);
                on.setVisibility(View.GONE);
            });





        pickimage = findViewById(R.id.pickimage);
        pickimage.setOnClickListener(view -> {
                Intent pickimg = new Intent(Intent.ACTION_PICK);
                pickimg.setType("image/*");
                imagpicker.launch(pickimg);
        });

        barcodeScannerView.decodeSingle(callback);

       camerapermission();


//        launch();

        }catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

   private void  camerapermission(){
        boolean hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        if (!hasCameraPermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_REQUEST);
        }
        else
        {
            launch();
        }

    }


    private void askpermissionagin()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Camera");
        builder.setMessage("Permission required to scan QR codes and Barcodes")
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       setting();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).create().show();

    }





    private void setting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(),null);
        intent.setData(uri);
        startActivity(intent);
    }





    private void launch(){
        options = new ScanOptions();
        options.setOrientationLocked(true);
        options.setBeepEnabled(true);
        options.setCaptureActivity(ScannerActivity.class);
        barcodeLauncher.launch(options);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CAMERA_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               launch();
            }
            else
            {
                askpermissionagin();
            }
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();

    }


    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        barcodeLauncher.unregister();
        finishAffinity();
    }


}