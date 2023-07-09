package com.innovativetools.scanner;

import static com.innovativetools.scanner.Constants.WRITE_EXTERNAL_STORAGE_REQUEST;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.zxing.Result;
import com.google.zxing.client.result.AddressBookParsedResult;
import com.google.zxing.client.result.SMSParsedResult;
import com.google.zxing.client.result.WifiParsedResult;
import com.innovativetools.scanner.generatecodes.Contents;
import com.innovativetools.scanner.utils.GenerateCodeTask;
import com.innovativetools.scanner.utils.QRGeneratorUtils;
import com.innovativetools.scanner.utils.ScannedResutUtils;
import com.innovativetools.scanner.viewmodels.Resultview;
import com.journeyapps.barcodescanner.BarcodeResult;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

import kotlin.jvm.JvmStatic;

public class Results extends AppCompatActivity{

    private static BarcodeResult barcodeResult = null;

    private Resultview viewModel;


    Uri uri = null;

    Bitmap bitmap;

    AddressBookParsedResult result;
    WifiParsedResult wifiresult;
    SMSParsedResult smsParsedResult;


    public static void startResultActivity(@NonNull Context context, @NonNull BarcodeResult barcodeResult) {
        Results.barcodeResult = barcodeResult;
//      Results.historyItem = null;
        Intent resultIntent = new Intent(context, Results.class);
        context.startActivity(resultIntent);
    }





    private boolean showformatedresult(){
        LinearLayout addcontact,call,email , location,open_url,copy_pass,web_search,add_event,send_sms,   share,copy,share_qr,save_qr;
        open_url = findViewById(R.id.open_url);
        copy_pass = findViewById(R.id.copy_pass);
        web_search = findViewById(R.id.web_search);
        addcontact = findViewById(R.id.add_person);
        call = findViewById(R.id.call);
        email = findViewById(R.id.send_email);
        location = findViewById(R.id.location);
        add_event = findViewById(R.id.event);
        send_sms = findViewById(R.id.send_sms);



        ImageView scannedimage = findViewById(R.id.scanned_image);
        ImageView scannedbarcode = findViewById(R.id.scanned_barcode);

        send_sms.setOnClickListener(view -> {

            smsParsedResult = (SMSParsedResult)viewModel.parsedResult;
            Uri smsnum = Uri.parse("smsto:+"+smsParsedResult.getNumbers()[0]);
            Intent intent = new Intent(Intent.ACTION_SENDTO, smsnum);
            intent.putExtra("sms_body",smsParsedResult.getBody());
            startActivity(intent);
        });



//      ImageView scannedbarcode = findViewById(R.id.scanned_barcode);
        share_qr = findViewById(R.id.share_qrcode);
        save_qr = findViewById(R.id.save_qrcode);
        copy = findViewById(R.id.copy);
        share = findViewById(R.id.share_text);


//        bitmap = ((BitmapDrawable)scannedimage.getDrawable()).getBitmap();



        TextView time = findViewById(R.id.date_created);
        TextView code_type = findViewById(R.id.result_type);
        TextView finalresult = findViewById(R.id.final_result);




        copy.setOnClickListener(view -> {
            ScannedResutUtils.copyResult(this,viewModel.parsedResult.getDisplayResult().toString());
        });
        share.setOnClickListener(view -> {
            ScannedResutUtils.shareResult(this,viewModel.parsedResult.getDisplayResult());
        });

        share_qr.setOnClickListener(view -> {
         QRGeneratorUtils.shareImage(this,QRGeneratorUtils.getCachedUri());
        });

        save_qr.setOnClickListener(view -> {

            try {
                Drawable image = null;
                Bitmap bitmap = null;
                if(scannedimage.getVisibility() == View.VISIBLE){
                 image = scannedimage.getDrawable();
                  bitmap  = ((BitmapDrawable)scannedimage.getDrawable()).getBitmap();;
                }else{
                    image = scannedbarcode.getDrawable();
                    bitmap = ((BitmapDrawable)scannedbarcode.getDrawable()).getBitmap();
                }

                if (image != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        QRGeneratorUtils.cacheImage(getApplicationContext(), bitmap);
                        QRGeneratorUtils.saveCachedImageToExternalStorage(getApplicationContext());
                        Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
                    }
                else
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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

                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        });

        web_search.setOnClickListener(view -> {
                Uri uri = null;
                try {
                    uri = (Uri.parse("https://www.google.com/search?q="+viewModel.parsedResult.getDisplayResult()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(Intent.ACTION_VIEW,uri));
        });

        open_url.setOnClickListener(view -> {
            uri = Uri.parse(viewModel.parsedResult.getDisplayResult());
            openurl(uri);
        });

        addcontact.setOnClickListener(view -> {
           String num = viewModel.parsedResult.getDisplayResult();
            addperson(num);

        });

        email.setOnClickListener(view -> {
          String  me =  viewModel.parsedResult.getDisplayResult();
            sendemail(me);
        });

        location.setOnClickListener(view -> {
            String address =viewModel.parsedResult.getDisplayResult();
            openmap(address);
        });

        copy_pass.setOnClickListener(view ->
        {
            wifiresult = (WifiParsedResult)viewModel.parsedResult;
            String text = wifiresult.getPassword();
            copypass(text);
        });


        code_type.setText(Contents.Type.parseParsedResultType(viewModel.parsedResult.getType()).toLocalizedString(getApplicationContext()));
        long timestamp = viewModel.currentBarcodeResult.getResult().getTimestamp();

        if (timestamp != 0) {
            DateFormat df = DateFormat.getDateTimeInstance();
            time.setText(df.format(new Date(timestamp)));
        }

        finalresult.setText(viewModel.parsedResult.getDisplayResult());
        new GenerateCodeTask(scannedimage, findViewById(R.id.progressbar), 500, 500, barcodeResult.getBarcodeFormat(), barcodeResult.getResultMetadata()).execute(barcodeResult.getText());

        if(viewModel.parsedResult.getType().toString().equals(Contents.Type.PRODUCT.toString())){
            scannedimage.setVisibility(View.GONE);
            scannedbarcode.setVisibility(View.VISIBLE);
            new GenerateCodeTask(scannedbarcode, findViewById(R.id.progressbar), 500, 500, barcodeResult.getBarcodeFormat(), barcodeResult.getResultMetadata()).execute(barcodeResult.getText());
        }

        if(viewModel.parsedResult == null){
            Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
        }

        switch (viewModel.parsedResult.getType()){

            case TEXT:
               web_search.setVisibility(View.VISIBLE);
              return true;

            case WIFI:
                copy_pass.setVisibility(View.VISIBLE);
             break;

            case ADDRESSBOOK:
                open_url.setVisibility(View.VISIBLE);
                email.setVisibility(View.VISIBLE);
                addcontact.setVisibility(View.VISIBLE);
                location.setVisibility(View.VISIBLE);

                result = (AddressBookParsedResult)viewModel.parsedResult;

                open_url.setOnClickListener(view -> {
                    uri = Uri.parse(result.getURLs()[0]);
                    openurl(uri);
                });
                addcontact.setOnClickListener(view -> {

                    String num = "";
                    String mail = "";
                    String comp = "";
                    String title = "";
                    String name = "";
                    try{
                     num  = result.getPhoneNumbers()[0];
                      mail = result.getEmails()[0];
                        comp = result.getOrg();
                        title = result.getTitle();
                        name  = result.getNames()[0];

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    addperson(num,name,mail,comp,title);


                });
                email.setOnClickListener(view -> {
                    String me = "";
                    try {
                        me = result.getEmails()[0];
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    sendemail(me);
                });
                location.setOnClickListener(view -> {
                    String address = result.getAddresses()[0];
                    openmap(address);
                });

                break;

            case EMAIL_ADDRESS:
                 email.setVisibility(View.VISIBLE);
                break;

            case PRODUCT:
                web_search.setVisibility(View.VISIBLE);
                break;

            case URI:
                 open_url.setVisibility(View.VISIBLE);

                 if(preferences.getBoolean("autoopenurl",false))
                 {
                     uri = Uri.parse(viewModel.parsedResult.getDisplayResult());
                     openurl(uri);
                 }

                 break;
            case GEO:
                location.setVisibility(View.VISIBLE);
                break;
            case TEL:
               addcontact.setVisibility(View.VISIBLE);
               call.setVisibility(View.VISIBLE);
                break;
            case SMS:
                  send_sms.setVisibility(View.VISIBLE);
                break;

            case CALENDAR:
                add_event.setVisibility(View.VISIBLE);
                break;

            case ISBN:

            case VIN:

        }
        return false;
    }

    private void copypass(String text) {
        ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("copied",text);
        clipboardManager.setPrimaryClip(clip);
        Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show();
    }

    private void openmap(String address) {
        try {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + address);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void addperson(String num) {
        try {
            Intent contact = new Intent(ContactsContract.Intents.Insert.ACTION);
            contact.setType(ContactsContract.RawContacts.CONTENT_TYPE);
            contact.putExtra(ContactsContract.Intents.Insert.PHONE, num);
            startActivity(contact);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void addperson(String num , String name , String email , String company , String title) {
        try {
            Intent contact = new Intent(ContactsContract.Intents.Insert.ACTION);
            contact.setType(ContactsContract.RawContacts.CONTENT_TYPE);
            contact.putExtra(ContactsContract.Intents.Insert.PHONE, num);
            contact.putExtra(ContactsContract.Intents.Insert.EMAIL,email);
            contact.putExtra(ContactsContract.Intents.Insert.EMAIL,email);
            contact.putExtra(ContactsContract.Intents.Insert.NAME, name);
            contact.putExtra(ContactsContract.Intents.Insert.COMPANY, company);
            contact.putExtra(ContactsContract.Intents.Insert.JOB_TITLE,title);
            startActivity(contact);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendemail(String me ) {
        try {
            Intent mail = new Intent(Intent.ACTION_SENDTO, Uri
                    .fromParts("mailto",me, null));
            startActivity(Intent.createChooser(mail, null));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void openurl(Uri uri) {
        try{
            ScannedResutUtils.openUrl(this,uri);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#ffffff\">" + "Result" + "</font>"));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);






        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

         viewModel = new ViewModelProvider(this).get(Resultview.class);

         viewModel.initFromScan(barcodeResult);

         showformatedresult();

         initStateIfNecessary(savedInstanceState);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,ScannerActivity.class));
    }

    private void initStateIfNecessary(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
                if (barcodeResult != null) {
                viewModel.initFromScan(barcodeResult);
            } else {
                Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
//                finish();
            }
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