package org.opensecurity.sms;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.opensecurity.sms.RSA.areKeysPresent;
import static org.opensecurity.sms.RSA.generateKey;

public class OpenSecuritySMS extends AppCompatActivity {
    private ListView list;
    private ListView vue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_security_sms);

        vue = (ListView) findViewById(R.id.vue);
        String [][] repertoire = new String[][] {   {"Contact Name", "Text of the latest message"},
                                                    {"Contact Name", "Text of the latest message"},
                                                    {"Contact Name", "Text of the latest message"},
                                                    {"Contact Name", "Text of the latest message"},
                                                    {"Contact Name", "Text of the latest message"},
                                                    {"Contact Name", "Text of the latest message"},
                                                    {"Contact Name", "Text of the latest message"},
                                                    {"Contact Name", "Text of the latest message"}};
        List<HashMap<String, String>> liste = new ArrayList<HashMap<String,String>>();
        HashMap<String,String> element;

        for(int i=0; i<repertoire.length; i++) {
            element = new HashMap<String,String>();
            element.put("text1", repertoire[i][0]);
            element.put("text2", repertoire[i][1]);
            liste.add(element);
        }
        ListAdapter listeWidget = new SimpleAdapter(this, liste, android.R.layout.simple_list_item_2, new String[]{"text1","text2"},
                                                    new int[] {android.R.id.text1, android.R.id.text2});

        vue.setAdapter(listeWidget);
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
