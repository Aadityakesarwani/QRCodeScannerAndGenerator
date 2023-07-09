package com.innovativetools.scanner;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class Constants {

   public static final int WRITE_EXTERNAL_STORAGE_REQUEST = 101;
   public static final String[] errorCorrectionsQR = new String[]{ErrorCorrectionLevel.L.name(), ErrorCorrectionLevel.M.name(), ErrorCorrectionLevel.Q.name(), ErrorCorrectionLevel.H.name()};
}
