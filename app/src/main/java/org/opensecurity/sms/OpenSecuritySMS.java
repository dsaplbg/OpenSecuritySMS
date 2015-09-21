package org.opensecurity.sms;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.opensecurity.sms.RSA.areKeysPresent;
import static org.opensecurity.sms.RSA.generateKey;

public class OpenSecuritySMS extends AppCompatActivity {
    private ListView list;
    private ListView listeConversations;
    private ListView vue;
    private List<ConversationLine> convers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_security_sms);

        //create a ArrayList of ConversationLine object.
        convers = new ArrayList<ConversationLine>();
        listeConversations = (ListView)findViewById(R.id.listeConvers);
        convers.add(new ConversationLine("Contact Name", "LatestMessage"));
        convers.add(new ConversationLine("Contact Name", "LatestMessage"));
        convers.add(new ConversationLine("Contact Name", "LatestMessage"));
        convers.add(new ConversationLine("Contact Name", "LatestMessage"));
        convers.add(new ConversationLine("Contact Name", "LatestMessage"));
        //ArrayConversAdapter est une class héritée de ArrayAdapter
        /*the listView listeConversations will be showed in the activity thanks to the
        Override of child class ArrayConversAdapter and getView method. and convers is
        the support. */
        listeConversations.setAdapter(new ArrayConversAdapter(this, convers));

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
