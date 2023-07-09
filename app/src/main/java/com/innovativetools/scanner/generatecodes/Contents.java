package com.innovativetools.scanner.generatecodes;

import android.content.Context;
import android.provider.ContactsContract;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.google.zxing.client.result.ParsedResultType;
import com.innovativetools.scanner.R;



public final class Contents {
    private Contents() {
    }

    public enum Type {
        UNDEFINED,

        TEXT,

        EMAIL,

        PHONE,

        SMS,
        MMS,
        WEB_URL,
        WIFI,
        V_CARD,
        ME_CARD,
        BIZ_CARD,
        MARKET,
        CONTACT,
        LOCATION,

        ADDRESSBOOK,
        PRODUCT,
        URI,
        CALENDAR,
        ISBN,
        VIN;


        public String toLocalizedString(Context context) {
            switch (this) {
                case TEXT:
                    return context.getResources().getStringArray(R.array.content_types)[1];
                case EMAIL:
                    return context.getResources().getStringArray(R.array.content_types)[2];
                case PHONE:
                    return context.getResources().getStringArray(R.array.content_types)[3];
                case SMS:
                    return context.getResources().getStringArray(R.array.content_types)[4];
                case MMS:
                    return context.getResources().getStringArray(R.array.content_types)[5];
                case WEB_URL:
                    return context.getResources().getStringArray(R.array.content_types)[6];
                case WIFI:
                    return context.getResources().getStringArray(R.array.content_types)[7];
                case V_CARD:
                    return context.getResources().getStringArray(R.array.content_types)[8];
                case ME_CARD:
                    return context.getResources().getStringArray(R.array.content_types)[9];
                case BIZ_CARD:
                    return context.getResources().getStringArray(R.array.content_types)[10];
                case MARKET:
                    return context.getResources().getStringArray(R.array.content_types)[11];
                case CONTACT:
                    return context.getResources().getStringArray(R.array.content_types)[12];
                case LOCATION:
                    return context.getResources().getStringArray(R.array.content_types)[13];
                case ADDRESSBOOK:
                    return context.getResources().getStringArray(R.array.content_types)[14];
                case PRODUCT:
                    return context.getResources().getStringArray(R.array.content_types)[15];
                case URI:
                    return context.getResources().getStringArray(R.array.content_types)[6];
                case CALENDAR:
                    return context.getResources().getStringArray(R.array.content_types)[16];
                case ISBN:
                    return context.getResources().getStringArray(R.array.content_types)[17];
                case VIN:
                    return context.getResources().getStringArray(R.array.content_types)[18];
                default:
                    return context.getResources().getStringArray(R.array.content_types)[0];
            }
        }



        public static Type parseParsedResultType(@NonNull ParsedResultType type) {
            switch (type) {
                case ADDRESSBOOK:
                    return ADDRESSBOOK;
                case EMAIL_ADDRESS:
                    return EMAIL;
                case PRODUCT:
                    return PRODUCT;
                case URI:
                    return URI;
                case TEXT:
                    return TEXT;
                case GEO:
                    return LOCATION;
                case TEL:
                    return PHONE;
                case SMS:
                    return SMS;
                case CALENDAR:
                    return CALENDAR;
                case WIFI:
                    return WIFI;
                case ISBN:
                    return ISBN;
                case VIN:
                    return VIN;
                default:
                    return Type.UNDEFINED;
            }
        }
    }

    public static final String URL_KEY = "URL_KEY";

    public static final String NOTE_KEY = "NOTE_KEY";

    // When using Type.CONTACT, these arrays provide the keys for adding or retrieving multiple
    // phone numbers and addresses.
    public static final String[] PHONE_KEYS = {
            ContactsContract.Intents.Insert.PHONE, ContactsContract.Intents.Insert.SECONDARY_PHONE,
            ContactsContract.Intents.Insert.TERTIARY_PHONE
    };

    public static final String[] PHONE_TYPE_KEYS = {
            ContactsContract.Intents.Insert.PHONE_TYPE,
            ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE,
            ContactsContract.Intents.Insert.TERTIARY_PHONE_TYPE
    };

    public static final String[] EMAIL_KEYS = {
            ContactsContract.Intents.Insert.EMAIL, ContactsContract.Intents.Insert.SECONDARY_EMAIL,
            ContactsContract.Intents.Insert.TERTIARY_EMAIL
    };

    public static final String[] EMAIL_TYPE_KEYS = {
            ContactsContract.Intents.Insert.EMAIL_TYPE,
            ContactsContract.Intents.Insert.SECONDARY_EMAIL_TYPE,
            ContactsContract.Intents.Insert.TERTIARY_EMAIL_TYPE
    };
}
