package com.innovativetools.scanner.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ScannedResutUtils {


    private static void handleException(AppCompatActivity activity){
        if(activity == null){
           throw new IllegalArgumentException();
        }
    }
    public static void shareResult(AppCompatActivity activity , String text){
        handleException(activity);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,text);
        activity.startActivity(Intent.createChooser(intent,"Share Via"));
    }

    public static void copyResult(AppCompatActivity activity ,String text){

        handleException(activity);

        ClipboardManager clipboardManager = (ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("copied",text);
        clipboardManager.setPrimaryClip(clip);
        Toast.makeText(activity, "Copied", Toast.LENGTH_SHORT).show();
    }

    public static void openUrl(AppCompatActivity activity, Uri uri){
        handleException(activity);
        activity.startActivity(new Intent(Intent.ACTION_VIEW).setData(uri));
    }

//    public static void opencontact(AppCompatActivity activity,String phone,){
//        handleException(activity);
//
//    }

}
