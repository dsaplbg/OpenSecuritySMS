package org.opensecurity.sms.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.opensecurity.sms.R;
import org.opensecurity.sms.controller.Controller;
import org.opensecurity.sms.model.Contact;
import org.opensecurity.sms.model.modelView.conversation.ArrayBubbleAdapter;
import org.opensecurity.sms.model.modelView.conversation.Bubble;
import org.opensecurity.sms.model.modelView.conversation.ConversationItem;
import org.opensecurity.sms.model.modelView.listConversation.ConversationLine;

import java.util.ArrayList;
import java.util.Calendar;

public class ConversationActivity extends AppCompatActivity {

    private static ConversationActivity instance;

    private  ArrayBubbleAdapter adapter;

    private ArrayList<ConversationItem> bubbleData;
    private ConversationLine convers;
    private Contact contact;

    private SwipeMenuListView bubbleList;
    private EditText textMessage;
    private Button sendButton;

    public static ConversationActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_conversation);

        setBubbleData(new ArrayList<ConversationItem>());

        bubbleList = (SwipeMenuListView) findViewById(R.id.bubbleList);
        textMessage = (EditText) findViewById(R.id.textMessage);
        sendButton = (Button) findViewById(R.id.sendButton);

        update(getIntent());
    }


    @Override
    public void onStart() {
        super.onStart();
        instance = this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conversation, menu);

        MenuItem photo = menu.getItem(0);

        Bitmap origin = getContact().getPhoto(getContentResolver());
        Bitmap b = Bitmap.createScaledBitmap(origin, 2 * origin.getWidth(), 2 * origin.getHeight(), true);
        photo.setIcon(new BitmapDrawable(getResources(), b));

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
        } else if (id == R.id.action_photoContact) {
            Toast toast = Toast.makeText(getBaseContext(), getContact().getName() + "      " + getContact().getNumber(), Toast.LENGTH_SHORT);
            toast.show();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * method used to initialization of all listeners.
     * 1st listener : sendButton onClick.
     *
     */
    public void listeners() {

        /**
         * First listener. We call sendSMS function and after that, if the sms exists, we create a new
         * bubble and we reload the Adapter.
         */
        getSendButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textMessage.getText().length() > 0 && Controller.sendSMS(ConversationActivity.this.getBaseContext(), getConvers(), textMessage.getText().toString())) {
                    getBubbleData().add(new Bubble(textMessage.getEditableText().toString(), Calendar.getInstance(), true));
                    updadeBubble();
                    textMessage.setText("");
                }
            }
        });
    }

    private void receivedMessage(String message) {
        this.getBubbleData().add(new Bubble(message,
                Calendar.getInstance(),
                false));
        this.updadeBubble();
    }


    public void updadeBubble() {
        final ArrayBubbleAdapter adapter = new ArrayBubbleAdapter(ConversationActivity.this, ConversationActivity.this.getBubbleData());
        getBubbleList().setAdapter(adapter);
        getBubbleList().invalidate();
    }

    public ArrayList<ConversationItem> getBubbleData() {
        return bubbleData;
    }

    public void setBubbleData(ArrayList<ConversationItem> bubbleData) {
        this.bubbleData = bubbleData;
    }

    public SwipeMenuListView getBubbleList() {
        return bubbleList;
    }

    public void setBubbleList(SwipeMenuListView bubbleList) {
        this.bubbleList = bubbleList;
    }

    public void setSendButton(Button b) {this.sendButton = b;}

    public Button getSendButton() {return this.sendButton;}

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public ConversationLine getConvers() {
        return convers;
    }

    public void setConvers(ConversationLine convers) {
        this.convers = convers;
    }

    public void update(Intent intent) {
        if (intent.getSerializableExtra("ConversationLine") != null) setConvers((ConversationLine) intent.getSerializableExtra("ConversationLine"));
        else setConvers((ConversationLine) intent.getExtras().getSerializable("ConversationLine"));

        setContact(getConvers().getContact());
        this.setTitle(getContact().getName());

        setBubbleData(Controller.loadMessages(this.getContentResolver(), getContact(), getConvers().getMessageInTotal() - getConvers().getNumberLoaded(), ConversationLine.LIMIT_LOAD_MESSAGE));

        getBubbleList().setStackFromBottom(true);
        getBubbleList().setDividerHeight(0);
        adapter = new ArrayBubbleAdapter(this, getBubbleData());
        getBubbleList().setAdapter(adapter);

        //The displaying of hour when we swipe a bubble
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                if (getBubbleData().get(menu.getViewType()) instanceof Bubble) {
                    // create "open" item
                    SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
                    // set item background
                    openItem.setBackground((ColorDrawable) getBubbleList().getBackground());
                    // set item width
                    openItem.setWidth(150);
                    // set item title
                    openItem.setTitle(getBubbleData().get(menu.getViewType()).getManagedDate());
                    // set item title fontsize
                    openItem.setTitleSize(13);
                    // set item title font color
                    openItem.setTitleColor(Color.BLACK);
                    // add to menu
                    menu.addMenuItem(openItem);
                }
            }
        };

        bubbleList.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int prevVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int state) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (prevVisibleItem != firstVisibleItem && prevVisibleItem > firstVisibleItem && (getConvers().getMessageInTotal() - getConvers().getNumberLoaded() - 1) > 0) {
                    if (firstVisibleItem == 0) {

                    }
                }

                prevVisibleItem = firstVisibleItem;
            }
        });
        bubbleList.setMenuCreator(creator);

        listeners();
    }
}