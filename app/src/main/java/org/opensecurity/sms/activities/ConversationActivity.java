package org.opensecurity.sms.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.opensecurity.sms.R;
import org.opensecurity.sms.model.database.ContactDAO;
import org.opensecurity.sms.model.Contact;
import org.opensecurity.sms.model.Engine;
import org.opensecurity.sms.model.talk.ArrayBubbleAdapter;
import org.opensecurity.sms.model.talk.Bubble;
import org.opensecurity.sms.model.talk.ConversationItem;

import java.util.ArrayList;
import java.util.GregorianCalendar;


/**
 * This class is an activity for displaying one conversation bitween us and
 * another contact.
 */

public class ConversationActivity extends Activity {


    /**
     * instance of a conversation activity
     */
    private static ConversationActivity instance;

    /**
     * The contact who has been selected in conversation activity
     */
    private Contact contact;

    /**
     * The array to keep bubbles. We load messages into this array
     */
    private ArrayList<ConversationItem> bubbleData;

    /**
     * The adapter for this activity
     */
    private ArrayBubbleAdapter adapter;

    /**
     * The listeView for displaying bubbles
     */
    private SwipeMenuListView bubbleList;

    /**
     * The editText wiget to enter text to send
     */
    private EditText textMessage;

    private ContactDAO contactDAO;

    /**
     * the button used to send messages
     */
    private Button sendButton;

    /**
     * To return the current instance. Be sure that is the only one instance (singleton pattern)
     * @return the instance of current activity (=this)
     */
    public static ConversationActivity getInstance() {
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
        this.setContentView(R.layout.activity_conversation);

        this.bubbleData = new ArrayList<ConversationItem>();

        this.bubbleList = (SwipeMenuListView) findViewById(R.id.bubbleList);
        this.bubbleList.setStackFromBottom(true);
        this.bubbleList.setDividerHeight(0);
        //The displaying of hour when we swipe a bubble
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                if (getAdapter().getItem(menu.getViewType()) instanceof Bubble) {
                    // create "open" item
                    SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
                    // set item background
                    openItem.setBackground((ColorDrawable) getBubbleList().getBackground());
                    // set item width
                    openItem.setWidth(150);
                    // set item title
                    openItem.setTitle(((ConversationItem) getAdapter().getItem(menu.getViewType())).getManagedDate());
                    // set item title fontsize
                    openItem.setTitleSize(13);
                    // set item title font color
                    openItem.setTitleColor(Color.BLACK);
                    // add to menu
                    menu.addMenuItem(openItem);
                }
            }
        };
        this.bubbleList.setMenuCreator(creator);

        this.textMessage = (EditText) findViewById(R.id.textMessage);
        this.sendButton = (Button) findViewById(R.id.sendButton);

        listeners();

        update(getIntent());



        contactDAO = new ContactDAO(getApplicationContext());
        contactDAO.openDb();

        if(contactDAO.findContactByPhoneNumberInOSMSBase(contact.getNumber()) == null)
            contactDAO.insertContactIntoDB(contact);

        instance = this;
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
        getMenuInflater().inflate(R.menu.menu_conversation, menu);
        MenuItem photo = menu.getItem(0);

        Bitmap origin = getContact().getPhoto(getContentResolver());
        Bitmap b = Bitmap.createScaledBitmap(origin, 2 * origin.getWidth(), 2 * origin.getHeight(), true);
        photo.setIcon(new BitmapDrawable(getResources(), b));

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
        } else if (id == R.id.action_photoContact) {
            Toast toast = Toast.makeText(getBaseContext(), getContact().getName() + "      " + getContact().getNumber(), Toast.LENGTH_SHORT);
            toast.show();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * contains listeners of the activity
     */
    public void listeners() {

        /**
         * First listener. We call sendSMS function and after that, if the sms exists, we create a new
         * bubble and we reload the Adapter.
         */
        getSendButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textMessage.getText().length() > 0 && Engine.getInstance().sendSMS(getBaseContext(), getContact(),
                        textMessage.getText().toString())) {
                    ConversationActivity.getInstance().update();
                    textMessage.setText("");
                }
            }
        });
    }

    /**
     * Just set the ArrayList of Bubbles in the activity.
     * @param bubbleData the data that we want to load
     */
    public void setBubbleData(ArrayList<ConversationItem> bubbleData) {
        getBubbleData().clear();
        getBubbleData().addAll(bubbleData);

        this.adapter = new ArrayBubbleAdapter(getBaseContext(), this.getBubbleData());
        if (!getBubbleData().isEmpty()) this.bubbleList.setAdapter(this.adapter);
    }

    /**
     * Just return the bubbleData which are loaded in the activity
     * @return bubbleData of current activity
     */
    public ArrayList<ConversationItem> getBubbleData() {
        return this.bubbleData;
    }

    /**
     * To return the menuListView of bubbles.
     * @return the ListView of current activity
     */
    public SwipeMenuListView getBubbleList() {
        return bubbleList;
    }

    /**
     * To return the sendButton of this activity.
     * @return the Button to send messages in this activity
     */
    public Button getSendButton() {return this.sendButton;}

    /**
     * To get the contact of our current activity.
     * @return the current contact of selected activity conversation
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * To set the current contact when we select it in previous activity
     * @param contact the future current contact of this activity conversation.
     */
    public void setContact(Contact contact) {
        this.contact = contact;
        this.setTitle(contact.getName());
    }


    public void createBubble() {
        Bubble b = new Bubble(textMessage.getText().toString(), GregorianCalendar.getInstance(), true);
        bubbleData.add(b);
        this.bubbleList.setAdapter(this.adapter);
    }


    /**
     *
     * @param intent
     */
    public void update(Intent intent) {
        if (intent.getSerializableExtra("Contact") != null)
            setContact((Contact) intent.getSerializableExtra("Contact"));

        update();
    }

    /**
     * This function is used to update a conversation when it's necessary
     */
    public void update() {
        setBubbleData(Engine.getInstance().loadMessages(this.getContentResolver(), getContact(), 0, 1000000));
        /*
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
        });*/
    }

    /**
     * To get the adapter (design)
     * @return the adapter of current activity
     */
    public ArrayBubbleAdapter getAdapter() {
        return this.adapter;
    }

    @Override
    protected void onResume() {
        contactDAO.openDb();
        this.update();
        super.onResume();
    }

    @Override
    protected void onPause() {
        contactDAO.closeDb();
        super.onPause();
    }
}