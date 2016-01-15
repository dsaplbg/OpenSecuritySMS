package org.opensecurity.sms.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.opensecurity.sms.R;
import org.opensecurity.sms.model.DAO;
import org.opensecurity.sms.model.modelView.listConversation.ArrayConversAdapter;
import org.opensecurity.sms.model.modelView.listConversation.ConversationLine;

import java.util.ArrayList;

/**
 * main activity when we start the application.
 */
public class OpenSecuritySMS extends AppCompatActivity {

    /**
     * singleton pattern. We keep one instance (this) for the current activity
     */
    private static OpenSecuritySMS instance;

    /**
     * the listView of conversations
     */
    private ListView conversationList;

    /**
     * The arrayList to keep objects of conversationLine
     */
    private ArrayList<ConversationLine> conversationLines;

    /**
     * The adapter for design the current activity
     */
    private ArrayConversAdapter adapter;

    /**
     * to get the current instance (=this)
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

        this.conversationLines = new ArrayList<>();
        this.adapter = new ArrayConversAdapter(getBaseContext(), this.conversationLines);

        this.conversationList = (ListView) findViewById(R.id.listeConvers);
        this.conversationList.setAdapter(this.adapter);

        instance = this;

        update();
        listeners();

        DAO.getInstance().openDb();
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
        getMenuInflater().inflate(R.menu.menu_open_security_sm, menu);
        return true;
    }

    /**
     * To react when we select on item on the menu.
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

        return super.onOptionsItemSelected(item);
    }

    /**
     * Allow the activity to keep the list of conversationsLine after the death of this activiy
     * @param b the bundel used to serialize informations.
     */
    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putSerializable("ConversSerialization", getConversationLines());
    }

    /**
     * To resore data which was keeped by the bundle on onSaveInstanceState function
     * @param savedInstanceState the bundle
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        try {
            setConversationLines((ArrayList<ConversationLine>) savedInstanceState.getSerializable("ConversSerialization"));
        }
        catch (Exception e) {
            System.err.println("Error : the arrayList of conversationLine can't be saved !");
        }
    }


    /**
     * to return the current listView.
     * @return the current listView
     */
    public ListView getConversationList() {
        return conversationList;
    }

    /**
     * to return the data of conversations (arrayList)
     * @return the arrayList of conversationLines
     */
    public ArrayList<ConversationLine> getConversationLines() {
        return conversationLines;
    }

    /**
     * to set the data of conversationLines
     * @param conversationLines arrayList of conversationLines
     */
    public void setConversationLines(ArrayList<ConversationLine> conversationLines) {
        getConversationLines().clear();
        getConversationLines().addAll(conversationLines);
        getAdapter().notifyDataSetChanged();
    }

    /**
     * This function is used to update this activity when it's necessary.
     */
    public void update() {
        setConversationLines(DAO.getInstance().loadLastMessages(this.getContentResolver()));
        instance = this;

        /**
         * The listView conversationList will be showed in the activity thanks to the
         * Override of child class ArrayConversAdapter and getView method. and conversationLines is
         * the support(data of conversationLine information).
         */


    }


    /**
     * We write all listeners for current activity in this function.
     *
     * first listener : clickListeners on listView
     */
    public void listeners() {
        //starting the new activity when clicking on one of rowview.
        getConversationList().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                intent.putExtra("Contact", getConversationLines().get(position).getContact());
                startActivity(intent);
            }
        });
    }
    /**
     * To get the adapter (design)
     * @return the adapter of current activity
     */
    public ArrayConversAdapter getAdapter() {
        return adapter;
    }
}