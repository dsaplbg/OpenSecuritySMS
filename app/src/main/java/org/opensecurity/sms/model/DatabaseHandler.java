package org.opensecurity.sms.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by calliste on 15/01/16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String PHOTO_URL = "photoUrl";
    public static final String PUBLIC_RSA_KEY = "publicRsaKey";
    public static final String CONTACT_NAME = "contactName";
    public static final String THREAD_ID = "threadId";
    public static final String NUMBER_OF_MESSAGE = "numberOfMessages";
    public static final String CONTACT_TABLE_NAME =  "CONTACTOSMS";

    public static final String CONTACT_CREATE_TABLE =
            "Create table " + CONTACT_TABLE_NAME +
            "(" + PHONE_NUMBER + " TEXT Primary key, " +
            PUBLIC_RSA_KEY + " TEXT, "+
            PHOTO_URL + " TEXT, " +
            CONTACT_NAME + " TEXT, " +
            THREAD_ID + " REAL, " +
            NUMBER_OF_MESSAGE + " REAL);";

    /**
     * the  string used to drop all our database.
     */
    public static String DROP_ALL_CONTACTOSMS_TABLE = "DROP TABLE IF EXISTS 'ContactOSMS'";

    public  DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory cursorFactory, int version) {
        super(context,name,cursorFactory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println(CONTACT_CREATE_TABLE);
        db.execSQL(CONTACT_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
