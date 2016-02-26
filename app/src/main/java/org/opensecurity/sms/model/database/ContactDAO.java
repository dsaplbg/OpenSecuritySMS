package org.opensecurity.sms.model.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;
import android.widget.Toast;

import org.opensecurity.sms.model.Contact;
import org.opensecurity.sms.model.discussion.Message;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 *
 * @author Colas Broux
 * @author Calliste Hanriat
 */
public class ContactDAO {
    private SQLiteDatabase database;
    private DatabaseHandler dataHandler;
    private Context currentContex;

    public ContactDAO(Context context) {
        setCurrentContex(context);
        dataHandler = new DatabaseHandler(getCurrentContex());
    }

    public void openDb(){
        this.database = dataHandler.getWritableDatabase();
    }

    public void closeDb() {
        dataHandler.close();
    }

    /**
     * this function fill a contact with a given phoneNumber
     * @param phoneNumber the phone number to fill the contact
     * @return the recently created contact
     */
    public Contact fillContact(String phoneNumber) {
        Contact contact = new Contact(phoneNumber);
        contact.setName(phoneNumber);
        ContentResolver cr = getCurrentContex().getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cur = cr.query(uri, null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                phoneNumber = phoneNumber.replace("+", "");
                phoneNumber = phoneNumber.replace(" ","");
                Log.d("phone", phoneNumber);
                String id = getContactRowIDLookupList(phoneNumber);
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                System.out.println("id of " + name + " is " + id);
                contact.setId(Integer.valueOf(id));
                contact.setName(name);
            }
        }
        cur.close();
        return contact;
    }

    /**
     * Gets a list of contact ids that is pointed at the passed contact number
     * parameter
     *
     * @param contactNo
     *            contact number whose contact Id is requested (no special chars)
     * @return String representation of a list of contact ids pointing to the
     *         contact in this format 'ID1','ID2','34','65','12','17'...
     */
    public String getContactRowIDLookupList(String contactNo) {
        String contactNumber = Uri.encode(contactNo);
        String contactIdList = new String();
        if (contactNumber != null) {
            Cursor contactLookupCursor = getCurrentContex().getContentResolver().query(
                    Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                            Uri.encode(contactNumber)),
                    new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID },
                    null, null, null);
            if (contactLookupCursor != null) {
                while (contactLookupCursor.moveToNext()) {
                    int phoneContactID = contactLookupCursor
                            .getInt(contactLookupCursor
                                    .getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                    if (phoneContactID > 0) {
                        contactIdList = "" + phoneContactID + ",";
                    }
                }
                if (contactIdList.endsWith(",")) {
                    contactIdList = contactIdList.substring(0,
                            contactIdList.length() - 1);
                }
            }
            contactLookupCursor.close();
        }
        return contactIdList;
    }


    public ArrayList<Contact> getAllContacts() {
        ArrayList<Contact> contactList = new ArrayList<>();

        Cursor cursor = getCurrentContex().getContentResolver().query(Uri.parse("content://sms"),
                new String[]{"DISTINCT " + Telephony.Sms.ADDRESS, Telephony.Sms.BODY,
                        Telephony.Sms.TYPE, Telephony.Sms.THREAD_ID, Telephony.Sms.DATE},
                Telephony.Sms.ADDRESS + " IS NOT NULL)" + "Group by (" + Telephony.Sms.THREAD_ID,
                null,
                null);

        if (cursor.moveToFirst()) {
            String phoneNumber;
            Contact contact;
            do {
                phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                contact = fillContact(phoneNumber);
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return contactList;
    }

    /**
     * To find a contact in our openSecurity dataBase
     * @param phoneNumber the number of our contact we want to find.
     * @return the contact we are looking for
     */
    public Contact findContactByPhoneNumberInOSMSBase(String phoneNumber) {
        try {
            Contact c = new Contact(phoneNumber);
            Cursor cursor = getDatabase().rawQuery("Select * FROM " +
                    DatabaseHandler.CONTACT_TABLE_NAME + " Where phoneNumber = ?",
                    new String[]{phoneNumber});

            cursor.moveToFirst();
            //we must use getCount to check if cursor contains something because a cursor is always != null
            if (cursor.getCount() == 0) {
                return null;
            }
            c.setPhotoURL(cursor.getString(2));
            c.setName(cursor.getString(3));
            c.setId(cursor.getInt(4));
            c.setNbMessages(cursor.getInt(5));

            Toast.makeText(currentContex, c.getName() + " found", Toast.LENGTH_LONG).show();
            return c;
        } catch (Exception e) {
            System.out.println("Erreur : je suis dans le catch" + e.toString());
        }
        return null;
    }

    /**
     * to insert a contact into the dataBase
     * @param c the context
     */
    public void insertContactIntoDB(Contact c) {
        try {
            getDatabase().beginTransaction();
            ContentValues value = new ContentValues();

            value.put(DatabaseHandler.CONTACT_NAME, c.getName());
            value.put(DatabaseHandler.NUMBER_OF_MESSAGE, c.getNbMessages());
            value.put(DatabaseHandler.PHONE_NUMBER, c.getPhoneNumber());
            value.put(DatabaseHandler.PHOTO_URL, c.getPhotoURL());
            value.put(DatabaseHandler.ID, c.getId());

            getDatabase().insert(DatabaseHandler.CONTACT_TABLE_NAME, null, value);
            getDatabase().setTransactionSuccessful();
        } catch (Exception e) {
            System.out.println("ERROR : Cannot insert contact ! ");
        } finally {
            getDatabase().endTransaction();
          //  Toast.makeText(currentContex.getApplicationContext(), "Contact inserted",
          // Toast.LENGTH_LONG).show();
        }
    }

    public void deleteAllContactOSMS(){
        try {
            getDatabase().execSQL("DELETE FROM " + DatabaseHandler.CONTACT_TABLE_NAME);
            //getDatabase().execSQL(DatabaseHandler.DROP_ALL_CONTACTOSMS_TABLE);
        } catch (Exception e) {
            Log.d("Error", "Dont able to delete all elements of "+
                    DatabaseHandler.CONTACT_TABLE_NAME);
        }
    }

    /**
     * This function has to return a contact Object thanks to a phoneNumber and an access
     * to the android dataBase
     *
     * @param phoneNumber     the phoneNumber of a Contact in your phone.
     * @param contentResolver to manage access to a structured set of data in your phone
     * @return the contact who has this phoneNumber
     */
    public Contact findContactByPhoneNumberInDefaultBase(String phoneNumber, ContentResolver
            contentResolver, HashMap<String, Contact> listContacts) {
        if (listContacts != null && listContacts.containsKey(phoneNumber)) return listContacts.get(phoneNumber);

        Contact contact = new Contact(phoneNumber);
        Uri personUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                phoneNumber);
        Cursor localCursor = contentResolver.query(personUri,
                new String[]{ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.Contacts.PHOTO_THUMBNAIL_URI},
                null,
                null,
                null);
        if (localCursor.moveToFirst()) {
            contact.setName(localCursor.getString(localCursor.getColumnIndexOrThrow(
                    ContactsContract.Contacts.DISPLAY_NAME)));
            contact.setPhotoURL(localCursor.getString(localCursor.getColumnIndexOrThrow(
                    ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)));
        }
        localCursor.close();

        return contact;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public Context getCurrentContex() {
        return currentContex;
    }

    public void setCurrentContex(Context currentContex) {
        this.currentContex = currentContex;
    }
}
