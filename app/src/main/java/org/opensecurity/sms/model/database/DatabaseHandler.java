package org.opensecurity.sms.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Calliste Hanriat
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static DatabaseHandler sInstance;

    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String PHOTO_URL = "photoUrl";
    public static final String PUBLIC_RSA_KEY = "publicRsaKey";
    public static final String CONTACT_NAME = "contactName";
    public static final String ID = "id";
    public static final String NUMBER_OF_MESSAGE = "numberOfMessages";
    public static final String CONTACT_TABLE_NAME =  "CONTACTOSMS";
    public static final String DBNAME = "opensecuritysms.db";
    private static final int DATABASE_VERSION = 1;
    public static final String CONTACT_CREATE_TABLE =
            "Create table " + CONTACT_TABLE_NAME +
            "(" + PHONE_NUMBER + " TEXT Primary key, " +
            PUBLIC_RSA_KEY + " TEXT, "+
            PHOTO_URL + " TEXT, " +
            CONTACT_NAME + " TEXT, " +
            ID + " REAL, " +
            NUMBER_OF_MESSAGE + " REAL);";

    /**
     * the  string used to drop all our database.
     */
    public static String DROP_ALL_CONTACTOSMS_TABLE = "DROP TABLE IF EXISTS " + CONTACT_TABLE_NAME + ";";


    public DatabaseHandler(Context context) {
        super(context,DBNAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("dataBase", "Created");
        db.execSQL(CONTACT_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_ALL_CONTACTOSMS_TABLE);
        onCreate(db);
    }
}
