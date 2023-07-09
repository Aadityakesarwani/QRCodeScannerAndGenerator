package com.innovativetools.scanner.createcodes;

import static com.innovativetools.scanner.Constants.WRITE_EXTERNAL_STORAGE_REQUEST;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.innovativetools.scanner.Constants;
import com.innovativetools.scanner.R;
import com.innovativetools.scanner.generatecodes.Contents;
import com.innovativetools.scanner.utils.QRCodeEncoder;
import com.innovativetools.scanner.utils.QRGeneratorUtils;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class Text extends AppCompatActivity  {

    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST = 19;
    ImageView outputqr,save,share;
    EditText qrtext;
    private static Uri cache = null;
    private static final String IMAGE_FOLDER = "Created QR Codes";


    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#ffffff\">" + "Text" + "</font>"));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        qrtext  = (EditText) findViewById(R.id.editText);
        outputqr = findViewById(R.id.generated_qr);
        save  = findViewById(R.id.save_generated_qr);
        share  = findViewById(R.id.share_generated_qr);



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
        if(item.getItemId() == R.id.cnfrm) {

            if (qrtext.getText().toString().isEmpty()) {
                Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
            } else {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(qrtext.getApplicationWindowToken(), 0);

                outputqr.setVisibility(View.VISIBLE);
                findViewById(R.id.textInputLayout).setVisibility(View.GONE);
                findViewById(R.id.outputlayout).setVisibility(View.VISIBLE);

                try {
                    Glide.with(this).asBitmap().load(createImage(this, qrtext.getText().toString(), Contents.Type.TEXT, BarcodeFormat.QR_CODE, Constants.errorCorrectionsQR[0])).into(new BitmapImageViewTarget(outputqr));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QRGeneratorUtils.purgeCacheFolder(this);
    }









    public static Uri cacheImage(Context context, Bitmap image) {
        if (image == null) {
            throw new IllegalArgumentException("Image must not be null");
        }
        File imageFilePath = new File(context.getCacheDir(), "images/");
        imageFilePath.mkdir();
        imageFilePath = new File(imageFilePath, buildFileString());
        File file = writeToFile(imageFilePath, image);
        cache = FileProvider.getUriForFile(context, "com.innovativetools.fileprovider", file);
        return cache;
    }

    public static Uri getCachedUri() {
        return cache;
    }

    public static Uri createImage(Context context, String qrInputText, Contents.Type qrType, BarcodeFormat barcodeFormat, String errorCorrectionLevel) {

        //Find screen size
        WindowManager manager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;

        //Encode with a QR Code image
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrInputText,
                null,
                qrType,
                barcodeFormat.toString(),
                smallerDimension);
        Bitmap bitmap_ = null;
        try {
            bitmap_ = qrCodeEncoder.encodeAsBitmap(errorCorrectionLevel);
            // return bitmap_;

        } catch (WriterException e) {
            e.printStackTrace();
        }

        return cacheImage(context, bitmap_);
    }

    public static void saveCachedImageToExternalStorage(Context context) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), cache);
            saveImageToExternalStorage(context, bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void saveImageToExternalStorage(Context context, Bitmap finalBitmap) throws IOException {
        final String fileName = buildFileString();

        OutputStream oStream;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = context.getContentResolver();
            Uri imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

            final String relativePath = Environment.DIRECTORY_PICTURES + File.separator + IMAGE_FOLDER;

            ContentValues newImage = new ContentValues();
            newImage.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            newImage.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            newImage.put(MediaStore.Images.ImageColumns.RELATIVE_PATH, relativePath);

            Uri imageUri = resolver.insert(imageCollection, newImage);
            oStream = resolver.openOutputStream(imageUri);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
        } else

        {

            File externalPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File myDir = new File(externalPath, IMAGE_FOLDER);
            myDir.mkdirs();
            File file = writeToFile(new File(myDir, fileName), finalBitmap);

            MediaScannerConnection.scanFile(context, new String[]{file.toString()}, null,
                    (path, uri) -> {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
            );
        }

    }


    private static @NonNull
    String buildFileString() {
        // Define name
        StringBuffer sb = new StringBuffer();
        sb.append("QrCode_");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.getDefault());
        sdf.format(Calendar.getInstance().getTime(), sb, new FieldPosition(SimpleDateFormat.DATE_FIELD));
        sb.append(".png");
        return sb.toString();
    }

    private static @Nullable
    File writeToFile(@NonNull File file, @NonNull Bitmap image) {
        File outFile = file;
        StringBuilder sb = new StringBuilder(file.toString());

        for (int i = 2; outFile.exists(); i++) {
            sb.delete(sb.length() - 4, sb.length());
            sb.append("_(").append(i).append(").png");
            outFile = new File(sb.toString());
        }

        try (FileOutputStream fOut = new FileOutputStream(outFile)) {
            image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return outFile;
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