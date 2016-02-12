package org.opensecurity.sms.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.opensecurity.sms.model.Contact;

/**
 * Created by loft-2015-asus on 24/01/16.
 */
public class ContactDAO {
    private SQLiteDatabase database;
    private Context currentContex;

    public ContactDAO(Context context) {
        currentContex = context;
    }

    public void openDb(){
        database = DatabaseHandler.getInstance(currentContex.getApplicationContext()).getWritableDatabase();
    }

    public void closeDb() {
        DatabaseHandler.getInstance(currentContex.getApplicationContext()).close();
    }

    /**
     * To find a contact in our openSecurity dataBase
     * @param phoneNumber the number of our contact we want to find.
     * @return the contact we are looking for
     */
    public Contact findContactByPhoneNumberInOSMSBase(String phoneNumber) {
        try {
            Contact c = new Contact(phoneNumber);
            Cursor cursor = database.rawQuery("Select * FROM " +
                    DatabaseHandler.CONTACT_TABLE_NAME + " Where phoneNumber = ?", new String[]{phoneNumber});
            if (cursor.equals(null)) {
                System.out.println("Je vaut null");
                return null;
            }
            cursor.moveToFirst();
            c.setPhotoURL(cursor.getString(2));
            c.setName(cursor.getString(3));
            c.setThreadId(cursor.getInt(4));
            c.setNbMessages(cursor.getInt(5));

          //  Toast.makeText(currentContex, c.getName() + " found", Toast.LENGTH_LONG).show();
            return c;
        } catch (Exception e) {
            System.out.println("Erreur : je suis dans le catch");
        }
        return null;
    }

    /**
     * to insert a contact into the dataBase
     * @param c the context
     */
    public void insertContactIntoDB(Contact c) {
        try {
            database.beginTransaction();
            ContentValues value = new ContentValues();

            value.put(DatabaseHandler.CONTACT_NAME, c.getName());
            value.put(DatabaseHandler.NUMBER_OF_MESSAGE, c.getNbMessages());
            value.put(DatabaseHandler.PHONE_NUMBER, c.getNumber());
            value.put(DatabaseHandler.PHOTO_URL, c.getPhotoURL());
            value.put(DatabaseHandler.THREAD_ID, c.getThreadId());

            database.insert(DatabaseHandler.CONTACT_TABLE_NAME, null, value);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            System.out.println("ERROR : Cannot insert contact ! ");
        } finally {
            database.endTransaction();
          //  Toast.makeText(currentContex.getApplicationContext(), "Contact inserted", Toast.LENGTH_LONG).show();
        }
    }

    public void deleteAllContactOSMS(){
        try {
            database.execSQL("DELETE FROM "+DatabaseHandler.CONTACT_TABLE_NAME);
        } catch (Exception e) {
            Log.d("Error", "Dont able to delete all elements of "+DatabaseHandler.CONTACT_TABLE_NAME);
        }
    }
}
