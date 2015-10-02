package org.opensecurity.sms;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.opensecurity.sms.fonctionnalKernel.Bubble;

import java.util.ArrayList;

public class ConversationActivity extends AppCompatActivity {
    private ArrayList<Bubble> bubbleData;
    private ListView bubbleList;
    private String convName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bubbleData = new ArrayList<>();
        bubbleList = (ListView) findViewById(R.id.bubbleList);

        Intent intent = getIntent();

        convName = intent.getStringExtra("Name");
        Toast.makeText(this, convName, Toast.LENGTH_LONG).show();
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
    
}