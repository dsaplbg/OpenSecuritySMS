package org.opensecurity.sms.activities;

import android.app.Activity;
import android.content.Intent;
import android.provider.Telephony;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.opensecurity.sms.R;
import org.opensecurity.sms.model.database.ContactDAO;
import org.opensecurity.sms.model.Engine;
import org.opensecurity.sms.model.discussion.Message;
import org.opensecurity.sms.model.discussion.ArrayDiscussionAdapter;

import java.util.ArrayList;

/**
 * main activity when we start the application.
 */
public class OpenSecuritySMS extends AppCompatActivity {

    public static final String SMS_DEFAULT_APPLICATION = "sms_default_application";
    private MenuItem itemDefaultApp;


    /**
     * singleton pattern. We keep one instance (this) for the current activity
     */
    private static OpenSecuritySMS instance;

    /**
     * the engine of main class
     */
    private Engine engine;

    /**
     * the listView of conversations
     */
    private ListView conversationList;

    /**
     * The floating action button
     */
    private FloatingActionButton fab;

    /**
     * The arrayList to keep objects of conversationLine
     */
    private ArrayList<Message> messages;

    /**
     * The adapter for design the current activity
     */
    private ArrayDiscussionAdapter adapter;

    /**
     * to get the current instance (=this)
     *
     * @return instance of our activity
     */
    public static OpenSecuritySMS getInstance() {
        return instance;
    }

    /**
     * To create the activity
     *
     * @param savedInstanceState save the instance state to keep informations.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_security_sms);

        this.messages = new ArrayList<>();
        this.adapter = new ArrayDiscussionAdapter(getBaseContext(), this.messages);

        this.conversationList = (ListView) findViewById(R.id.listeConvers);
        this.conversationList.setAdapter(this.adapter);

        this.fab = (FloatingActionButton) findViewById(R.id.fab);

        instance = this;
        setEngine(new Engine(this.getApplicationContext()));
        getEngine().getContactDAO().openDb();
        update();
        listeners();


        instance = this;


        final String myPackageName = getPackageName();
        if (!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {
            // App is not default.
            Intent intent =
                    new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                    myPackageName);
            startActivity(intent);
        }
    }

    /**
     * Use to create an optionMenu.
     *
     * @param menu keep informations relative to the menu
     * @return boolean if the menu is create or no
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_open_security_sms, menu);
        return true;
    }

    /**
     * To react when we select on item on the menu.
     *
     * @param item the current selected item.
     * @return
     */
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

        if (id == R.id.deleteAllElementsOfTable) {
            this.getEngine().getContactDAO().deleteAllContactOSMS();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Allow the activity to keep the list of conversationsLine after the death of this activiy
     *
     * @param b the bundel used to serialize informations.
     */
    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putSerializable("ConversSerialization", getMessages());
    }

    /**
     * To resore data which was keeped by the bundle on onSaveInstanceState function
     *
     * @param savedInstanceState the bundle
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        try {
            setMessages((ArrayList<Message>) savedInstanceState.getSerializable("ConversSerialization"));
        } catch (Exception e) {
            System.err.println("Error : the arrayList of conversationLine can't be saved !");
        }
    }


    /**
     * to return the current listView.
     *
     * @return the current listView
     */
    public ListView getConversationList() {
        return conversationList;
    }

    /**
     * to return the data of conversations (arrayList)
     *
     * @return the arrayList of messages
     */
    public ArrayList<Message> getMessages() {
        return messages;
    }

    /**
     * to set the data of messages
     *
     * @param messages arrayList of messages
     */
    public void setMessages(ArrayList<Message> messages) {
        getMessages().clear();
        getMessages().addAll(messages);
        getAdapter().notifyDataSetChanged();
    }

    /**
     * This function is used to update this activity when it's necessary.
     */
    public void update() {
        setMessages(this.getEngine().loadLastMessages(this.getContentResolver()));
    }


    /**
     * We write all listeners for current activity in this function.
     * <p/>
     * first listener : clickListeners on listView
     */
    public void listeners() {
        //starting the new activity when clicking on one of rowview.
        getConversationList().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                intent.putExtra("Contact", getMessages().get(position).getContact());
                startActivity(intent);
            }
        });

        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Click !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * To get the adapter (design)
     *
     * @return the adapter of current activity
     */
    public ArrayDiscussionAdapter getAdapter() {
        return adapter;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    @Override
    protected void onResume() {
        getEngine().getContactDAO().openDb();
        super.onResume();
    }


    @Override
    protected void onPause() {
        getEngine().getContactDAO().closeDb();
        super.onPause();
    }
}