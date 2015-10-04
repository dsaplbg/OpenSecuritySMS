package org.opensecurity.sms;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.opensecurity.sms.fonctionnalKernel.ArrayBubbleAdapter;
import org.opensecurity.sms.fonctionnalKernel.Bubble;
import org.opensecurity.sms.fonctionnalKernel.ConversationLine;
import org.opensecurity.sms.fonctionnalKernel.ArrayBubbleAdapter;

import java.util.ArrayList;
import java.util.List;

public class ConversationActivity extends AppCompatActivity {
    private ArrayList<Bubble> bubbleData;
    private ListView bubbleList;
    private ConversationLine cont;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_conversation);

        bubbleData = new ArrayList<>();
        bubbleList = (ListView) findViewById(R.id.bubbleList);
        System.out.println((bubbleList == null) + "\n" + (bubbleData == null));
        Intent intent = getIntent();

        cont = (ConversationLine) intent.getSerializableExtra("Contact");
        Toast.makeText(this, cont.getContactName(), Toast.LENGTH_LONG).show();
        this.displayMessages();

        bubbleList.setAdapter(new ArrayBubbleAdapter(this, bubbleData));
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
private void displayMessages() {
        ContentResolver cr = this.getContentResolver();
        String date;
        String content;
        boolean isMe;

        try {
            Cursor cursor = cr.query(Uri.parse("content://sms"), null, this.cont.getThread_ID() + " = thread_id" , null, "date ASC");

            while (cursor.moveToNext()) {
                //if it's a recevied message :
                if ((cursor.getString(cursor.getColumnIndexOrThrow("person")) != null)) {
                    isMe = false;
                    //System.out.println("Message from " + this.cont.getContactName());
                }
                else {
                    isMe = true;
                    //System.out.println("Message from moi");
                }
                content = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
             //   System.out.println("Number: " + cursor.getString(cursor.getColumnIndexOrThrow("address")));
             //   System.out.println("Body : " + content + "\n");

                this.bubbleData.add(new Bubble(content, date, isMe));
            }
            cursor.close();

        }
        catch (Exception e) {
            System.out.print("ERREUR");
        }
    }
}