package com.innovativetools.scanner.viewmodels;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;

import com.innovativetools.scanner.R;
import com.innovativetools.scanner.utils.Utilss;
import com.journeyapps.barcodescanner.BarcodeResult;

public class Resultview extends AndroidViewModel {

    private static final String PREF_SAVE_REAL_IMAGE_TO_HISTORY = "pref_save_real_image_to_history";
    public BarcodeResult currentBarcodeResult = null;
    public ParsedResult parsedResult = null;
    public Bitmap codeimg = null;

    private final SharedPreferences preferences;


    public Resultview(@NonNull Application application) {
        super(application);
        preferences = PreferenceManager.getDefaultSharedPreferences(application);
    }




    public void initFromScan(BarcodeResult barcodeResult) {
       currentBarcodeResult = barcodeResult;
        parsedResult = ResultParser.parseResult(currentBarcodeResult.getResult());
        fillMissingResultPoints();
        try { codeimg = currentBarcodeResult.getBitmapWithResultPoints(ContextCompat.getColor(getApplication(), R.color.blue));
        } catch (NullPointerException e) {
           codeimg = Utilss.generateCode(currentBarcodeResult.getText(), currentBarcodeResult.getBarcodeFormat(), null, currentBarcodeResult.getResult().getResultMetadata());
        }

    }

    private void fillMissingResultPoints() {
        if (currentBarcodeResult != null && currentBarcodeResult.getResultPoints() != null) {
            ResultPoint[] orig = currentBarcodeResult.getResultPoints();
            ResultPoint valid = new ResultPoint(0, 0);
            for (ResultPoint point : orig) {
                if (point != null) {
                    valid = point;
                    break;
                }
            }
            for (int i = 0; i < orig.length; i++) {
                if (orig[i] == null) {
                    orig[i] = valid;
                }
            }
        }
    }

}
