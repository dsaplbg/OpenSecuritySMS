package org.opensecurity.sms.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.opensecurity.sms.R;
import org.opensecurity.sms.controller.Controller;
import org.opensecurity.sms.model.modelView.conversation.ArrayBubbleAdapter;
import org.opensecurity.sms.model.modelView.conversation.Bubble;
import org.opensecurity.sms.model.modelView.conversation.ConversationItem;
import org.opensecurity.sms.model.modelView.listConversation.ConversationLine;

import java.util.ArrayList;
import java.util.Calendar;




public class ConversationActivity extends AppCompatActivity {
    /**
     * Message set action.
     */
    public static final String MESSAGE_SENT_ACTION = "com.android.mms.transaction.MESSAGE_SENT";
    public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private  ArrayBubbleAdapter adapter;

    private ArrayList<ConversationItem> bubbleData;
    private SwipeMenuListView bubbleList;
    private ConversationLine cont;
    private TextView contactName;
    private ImageView photoContact;
    private EditText textMessage;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_conversation);

        setBubbleData(new ArrayList<ConversationItem>());
        setBubbleList((SwipeMenuListView) findViewById(R.id.bubbleList));
        textMessage = (EditText) findViewById(R.id.textMessage);
        setSendButton((Button) findViewById(R.id.sendButton));
        Intent intent = getIntent();

        setCont((ConversationLine) intent.getSerializableExtra("Contact"));
        this.setTitle(cont.getContactName());

        setBubbleData(Controller.loadMessages(this.getContentResolver(), getCont(), getCont().getMessageInTotal() - getCont().getNumberLoaded(), ConversationLine.LIMIT_LOAD_MESSAGE));

        getBubbleList().setStackFromBottom(true);
        getBubbleList().setDividerHeight(0);
        adapter = new ArrayBubbleAdapter(this, getBubbleData());
        getBubbleList().setAdapter(adapter);

        textMessage = (EditText) findViewById(R.id.textMessage);

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
                if (prevVisibleItem != firstVisibleItem && prevVisibleItem > firstVisibleItem && (getCont().getMessageInTotal() - getCont().getNumberLoaded() - 1) > 0) {
                    if (firstVisibleItem == 0) {
                        if (!getCont().isReloaded()) {
                            /*getBubbleData().addAll(0, Controller.loadMessages(getContentResolver(), getCont(), getCont().getMessageInTotal() - getCont().getNumberLoaded(), 1));
                            getCont().setNumerLoaded(getCont().getNumberLoaded() + 1);*/
                            getCont().setReloaded(true);
                            Log.i("Val", "ok" + firstVisibleItem);
                            //adapter.notifyDataSetChanged();
                            //bubbleList.setSelection(1);
                            Log.i("Val", "ok" + getCont().getNumberLoaded() + "  " + firstVisibleItem);
                        }
                    } else getCont().setReloaded(false);
                }

                prevVisibleItem = firstVisibleItem;
            }
        });

        // set creator
        bubbleList.setMenuCreator(creator);
        listeners();

        IntentFilter filter = new IntentFilter(SMS_RECEIVED);
        registerReceiver(receiver_SMS, filter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conversation, menu);

        MenuItem photo = menu.getItem(0);

        Bitmap origin = cont.getPhoto(getContentResolver());
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
            Toast toast = Toast.makeText(getBaseContext(), cont.getContactName() + "      " + cont.getNumber(), Toast.LENGTH_SHORT);
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
                Controller.sendSMS(ConversationActivity.this.getBaseContext(),
                        ConversationActivity.this.getCont(),
                        ConversationActivity.this.textMessage.getText().toString());

                if (ConversationActivity.this.textMessage.getText().toString().length() > 0) {
                    ConversationActivity.this.getBubbleData().add(new Bubble(ConversationActivity.this.textMessage.getEditableText().toString(),
                            Calendar.getInstance(),
                            true));
                }
                ConversationActivity.this.updadeBubble();
                ConversationActivity.this.textMessage.setText("");
            }


        });
    }

    /**
     * Listener for an incoming sms in our app.
     * Use it when application has an incoming sms.
     * Verify if it's an SMS,
     * Verify if sms contains something (bundle != null)
     *
     */
    BroadcastReceiver receiver_SMS = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(ConversationActivity.this,  "Message recus ! ", Toast.LENGTH_LONG).show();

            if (intent.getAction().equals(SMS_RECEIVED)) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    SmsMessage[] messages = new SmsMessage[pdus.length];

                    for (int i = 0; i < pdus.length; i++)
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                    String message = new String("");

                    for (SmsMessage m : messages)
                        message = message+m.getDisplayMessageBody();
                    //if the message come from the current contact selected in the previous activity
                    String currentContactAddress = ConversationActivity.this.getCont().getNumber();
                    String receviedMessageAddress = messages[0].getOriginatingAddress();
                    receviedMessageAddress.replace(" ", "");
                    currentContactAddress.replace(" ", "");
                    if (receviedMessageAddress.equals(currentContactAddress)) {
//                        System.out.println("add 1 : " + receviedMessageAddress + "taille : " + receviedMessageAddress.length());
//                        System.out.println("add 2 : " + currentContactAddress + "taille : " + currentContactAddress.length());
//                        System.out.println("add 2 == add 1 : " + receviedMessageAddress.equals(currentContactAddress));
//                        System.out.println("Taille de messages :  " + messages.length);

                        receivedMessage(message);
                    }
                }
            }
        }
    };

    private void receivedMessage(String message) {
        this.getBubbleData().add(new Bubble(message,
                Calendar.getInstance(),
                false));
        this.updadeBubble();
    }


    public  void updadeBubble() {
        final ArrayBubbleAdapter adapter = new ArrayBubbleAdapter(ConversationActivity.this, ConversationActivity.this.getBubbleData());
        ConversationActivity.this.getBubbleList().setAdapter(adapter);
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

    public ConversationLine getCont() {
        return cont;
    }

    public void setCont(ConversationLine cont) {
        this.cont = cont;
    }

    public void setSendButton(Button b) {this.sendButton = b;}

    public Button getSendButton() {return this.sendButton;}
}