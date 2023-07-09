package com.innovativetools.scanner;

import static com.innovativetools.scanner.Constants.WRITE_EXTERNAL_STORAGE_REQUEST;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.client.result.AddressBookParsedResult;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.google.zxing.client.result.SMSParsedResult;
import com.google.zxing.client.result.WifiParsedResult;
import com.innovativetools.scanner.generatecodes.Contents;
import com.innovativetools.scanner.utils.GenerateCodeTask;
import com.innovativetools.scanner.utils.QRGeneratorUtils;
import com.innovativetools.scanner.utils.ScannedResutUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.function.LongToIntFunction;

public class Picked_img_result extends AppCompatActivity {

    private static Result result;
    private ParsedResult parsedResult;
    TextView finalresult;
    WifiParsedResult wifiresult;
    AddressBookParsedResult addrssresult;
    SMSParsedResult smsParsedResult;


    Bitmap bitmap;

    public static void pickedimgresult(Context context ,Result result){
        Picked_img_result.result = result;
        Intent intent = new Intent(context,Picked_img_result.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picked_img_result);


        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"#ffffff\">" + "Result" + "</font>"));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        parsedResult = ResultParser.parseResult(result);

        result();
    }


    private boolean result(){
        LinearLayout addcontact,call,email,location,open_url,copy_pass,web_search,add_event,send_sms,   share,copy,share_qr,save_qr;
        open_url = findViewById(R.id.open_url);
        copy_pass = findViewById(R.id.copy_pass);
        web_search = findViewById(R.id.web_search);
        addcontact = findViewById(R.id.add_person);
        call = findViewById(R.id.call);
        email = findViewById(R.id.send_email);
        location = findViewById(R.id.location);
        add_event = findViewById(R.id.event);
        send_sms = findViewById(R.id.send_sms);



        share_qr = findViewById(R.id.share_qrcode);
        save_qr = findViewById(R.id.save_qrcode);
        copy = findViewById(R.id.copy);
        share = findViewById(R.id.share_text);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        ImageView scannedimage = findViewById(R.id.scanned_image);
        TextView time = findViewById(R.id.date_created);
        TextView code_type = findViewById(R.id.result_type);
        finalresult = findViewById(R.id.final_result);


        email.setOnClickListener(view -> {
            try{
                String me = parsedResult.getDisplayResult();
                sendemail(me);
            }catch (Exception e){
                Log.d("mailerror",e.getMessage());
                e.printStackTrace();
            }
        });



        copy.setOnClickListener(view -> {
            ScannedResutUtils.copyResult(this,parsedResult.getDisplayResult());
        });

        share.setOnClickListener(view -> {
            ScannedResutUtils.shareResult(this,parsedResult.getDisplayResult());
        });

        share_qr.setOnClickListener(view -> {
            QRGeneratorUtils.shareImage(this,QRGeneratorUtils.getCachedUri());
        });


        save_qr.setOnClickListener(view -> {

            try {
                Drawable image = null;
                Bitmap bitmap = null;
                    image = scannedimage.getDrawable();
                    bitmap  = ((BitmapDrawable)scannedimage.getDrawable()).getBitmap();;

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
                uri = (Uri.parse("https://www.google.com/search?q="+parsedResult.getDisplayResult()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            startActivity(new Intent(Intent.ACTION_VIEW,uri));
        });


        open_url.setOnClickListener(view -> {
           Uri url = Uri.parse(parsedResult.getDisplayResult());
            openurl(url);
        });


        addcontact.setOnClickListener(view -> {

            try {
                Intent contact = new Intent(ContactsContract.Intents.Insert.ACTION);
                contact.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                contact.putExtra(ContactsContract.Intents.Insert.PHONE, parsedResult.getDisplayResult());
                startActivity(contact);
            }catch (Exception e){
                e.printStackTrace();
            }

        });


        copy_pass.setOnClickListener(view ->
        {
            wifiresult = (WifiParsedResult)parsedResult;
            String text = null;
            try {
                text = wifiresult.getPassword();
            }catch (Exception e){
                e.printStackTrace();
            }
            copypass(text);
        });


        location.setOnClickListener(view ->
        {
            String address = parsedResult.getDisplayResult();
            openmap(address);
        });


        send_sms.setOnClickListener(view -> {

            smsParsedResult = (SMSParsedResult)parsedResult;

            Uri smsnum = Uri.parse("smsto:+"+smsParsedResult.getNumbers()[0]);
            Intent intent = new Intent(Intent.ACTION_SENDTO, smsnum);
            intent.putExtra("sms_body",smsParsedResult.getBody());
            startActivity(intent);

        });


        code_type.setText(parsedResult.getType().toString());
        long timestamp = result.getTimestamp();

        DateFormat df = DateFormat.getDateTimeInstance();

        time.setText(df.format(new Date(timestamp)));
        finalresult.setText(parsedResult.getDisplayResult());
        new GenerateCodeTask(scannedimage, findViewById(R.id.progressbar), 500, 500, result.getBarcodeFormat(), result.getResultMetadata()).execute(result.getText());

        if(parsedResult == null){
            Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
        }

        switch (parsedResult.getType()){
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

                open_url.setOnClickListener(view -> {
                  Uri uri = Uri.parse(addrssresult.getURLs()[0]);
                    openurl(uri);
                });

                addcontact.setOnClickListener(view -> {
                    addrssresult = (AddressBookParsedResult)parsedResult;

                    String num = "";
                    String mail = "";
                    String comp = "";
                    String title = "";
                    String name = "";

                    try{
                        num  = addrssresult.getPhoneNumbers()[0];
                        mail = addrssresult.getEmails()[0];
                        comp = addrssresult.getOrg();
                        title = addrssresult.getTitle();
                        name  = addrssresult.getNames()[0];

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    addperson(num,name,mail,comp,title);
                });

                email.setOnClickListener(view -> {
                    String me = null;
                    try {
                        me = addrssresult.getEmails()[0];
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    sendemail(me);
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
                smsParsedResult = (SMSParsedResult) parsedResult;
                finalresult.setText(smsParsedResult.getSMSURI());
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
        try {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("copied", text);
            clipboardManager.setPrimaryClip(clip);
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,ScannerActivity.class));
        finish();
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