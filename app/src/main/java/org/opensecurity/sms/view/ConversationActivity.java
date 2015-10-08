package org.opensecurity.sms.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.opensecurity.sms.R;
import org.opensecurity.sms.controller.Controller;
import org.opensecurity.sms.model.ArrayBubbleAdapter;
import org.opensecurity.sms.model.Bubble;
import org.opensecurity.sms.model.ConversationLine;

import java.util.ArrayList;

public class ConversationActivity extends AppCompatActivity {
    private ArrayList<Bubble> bubbleData;
    private ListView bubbleList;
    private ConversationLine cont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_conversation);

        setBubbleData(new ArrayList<Bubble>());
        setBubbleList((ListView) findViewById(R.id.bubbleList));
        System.out.println((getBubbleList() == null) + "\n" + (getBubbleData() == null));
        Intent intent = getIntent();

        setCont((ConversationLine) intent.getSerializableExtra("Contact"));
        Toast.makeText(this, getCont().getContactName(), Toast.LENGTH_LONG).show();

        setBubbleData(Controller.loadMessages(this.getContentResolver(), getCont()));

        getBubbleList().setStackFromBottom(true);
        getBubbleList().setAdapter(new ArrayBubbleAdapter(this, getBubbleData()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conversation, menu);
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

    public ArrayList<Bubble> getBubbleData() {
        return bubbleData;
    }

    public void setBubbleData(ArrayList<Bubble> bubbleData) {
        this.bubbleData = bubbleData;
    }

    public ListView getBubbleList() {
        return bubbleList;
    }

    public void setBubbleList(ListView bubbleList) {
        this.bubbleList = bubbleList;
    }

    public ConversationLine getCont() {
        return cont;
    }

    public void setCont(ConversationLine cont) {
        this.cont = cont;
    }
}