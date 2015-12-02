package org.opensecurity.sms.view;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.opensecurity.sms.R;
import org.opensecurity.sms.controller.Controller;
import org.opensecurity.sms.model.Contact;
import org.opensecurity.sms.model.modelView.listConversation.ArrayConversAdapter;
import org.opensecurity.sms.model.modelView.listConversation.ConversationLine;

import java.util.ArrayList;

public class OpenSecuritySMS extends AppCompatActivity {

    private static OpenSecuritySMS instance;
    private ListView conversationList;
    private ArrayList<ConversationLine> conversationLines;

    public static OpenSecuritySMS getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_security_sms);
        setConversationList((ListView) findViewById(R.id.listeConvers));

        if(savedInstanceState != null) this.restore(savedInstanceState);
        else update();
    }

    @Override
    public void onStart() {
        super.onStart();
        instance = this;
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

    /**
     * Allow the activity to keep the list of conversationsLine after the death of this activiy
     * @param b
     */
    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putSerializable("ConversSerialization", getConversationLines());
    }

    public void restore(Bundle savedInstanceState) {
        try {
            setConversationLines((ArrayList<ConversationLine>) savedInstanceState.getSerializable("ConversSerialization"));
        }
        catch (Exception e) {
            System.err.println("Error : the arrayList of conversationLine can't be saved !");
        }
    }

    public ListView getConversationList() {
        return conversationList;
    }

    public void setConversationList(ListView conversationList) {
        this.conversationList = conversationList;
    }

    public ArrayList<ConversationLine> getConversationLines() {
        return conversationLines;
    }

    public ConversationLine getConversationLineByContact(Contact contact) {
        for (ConversationLine i : getConversationLines()) if (i.getContact().equals(contact)) return i;

        return null;
    }

    public void setConversationLines(ArrayList<ConversationLine> conversationLines) {
        this.conversationLines = conversationLines;
    }

    public void update() {
        setConversationLines(Controller.loadLastMessages(this.getContentResolver()));

        /**
         * The listView conversationList will be showed in the activity thanks to the
         * Override of child class ArrayConversAdapter and getView method. and conversationLines is
         * the support(data of conversationLine information).
         */
        getConversationList().setAdapter(new ArrayConversAdapter(this, getConversationLines()));

        //starting the new activity when clicking on one of rowview.
        getConversationList().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                intent.putExtra("ConversationLine", getConversationLines().get(position));
                startActivity(intent);
            }
        });

        getConversationList().invalidate();
    }
}