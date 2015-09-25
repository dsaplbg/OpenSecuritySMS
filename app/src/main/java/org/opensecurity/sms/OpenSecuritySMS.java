package org.opensecurity.sms;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.opensecurity.sms.fonctionnalKernel.ArrayConversAdapter;
import org.opensecurity.sms.fonctionnalKernel.ConversationLine;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class OpenSecuritySMS extends AppCompatActivity {
    private ListView listeConversations;
    private List<ConversationLine> convers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_security_sms);

        //create a ArrayList of ConversationLine object.
        convers = new ArrayList<ConversationLine>();
        listeConversations = (ListView)findViewById(R.id.listeConvers);
        //ArrayConversAdapter est une class héritée de ArrayAdapter
        /*the listView listeConversations will be showed in the activity thanks to the
        Override of child class ArrayConversAdapter and getView method. and convers is
        the support(data of conversationLine information). */

        ContentResolver cr = this.getContentResolver();
        // We want to get the sms in the inbox with all their attributes
        Cursor cursor = cr.query(Uri.parse("content://sms/inbox"),
                null,
                null,
                null,
                null);
        List<String> phoneNumbers = new ArrayList<String>();
        // While there is a message
        while (cursor.moveToNext()) {
            // We get the phoneNumber and the type of the message
            String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            int type = cursor.getInt(cursor.getColumnIndexOrThrow("type"));
            // if we don't already have this phoneNumber in the list and the message is not a draft
            if ((!phoneNumbers.contains(phoneNumber)) && (type != 3) && (phoneNumber.length()>=1)) {
                String name = null;
                // we get the smsContent and the date
                String smsContent = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                Date date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow("date"))));
                Uri personUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, phoneNumber);
                // in order to get the contact name, we do a query
                Cursor localCursor = cr.query(personUri,
                        new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                        null,
                        null,
                        null);
                if (localCursor.getCount() != 0) {
                    localCursor.moveToFirst();
                    name = localCursor.getString(localCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                }
                localCursor.close();
                phoneNumbers.add(phoneNumber);
                name = (name == null)?phoneNumber:name;
                String[] sms = new String []{name, phoneNumber, smsContent, date.toString()};
                // we add a new ConversationLine
                convers.add(new ConversationLine(name, smsContent, sms[3]));
            }
        }
        cursor.close();


        listeConversations.setAdapter(new ArrayConversAdapter(this, convers));

        //starting the new activity when clicking on one of rowview.
        listeConversations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(OpenSecuritySMS.this, convers.get(position).getContactName(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_open_security_sm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}