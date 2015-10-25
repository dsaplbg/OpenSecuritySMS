package org.opensecurity.sms.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
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
import org.opensecurity.sms.model.modelView.convesation.ArrayBubbleAdapter;
import org.opensecurity.sms.model.modelView.convesation.Bubble;
import org.opensecurity.sms.model.modelView.convesation.ConversationItem;
import org.opensecurity.sms.model.modelView.listConversation.ConversationLine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ConversationActivity extends AppCompatActivity {
    private ArrayList<ConversationItem> bubbleData;
    private SwipeMenuListView bubbleList;
    private ConversationLine cont;

    private TextView contactName;
    private ImageView photoContact;
    private EditText textMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_conversation);

        setBubbleData(new ArrayList<ConversationItem>());
        setBubbleList((SwipeMenuListView) findViewById(R.id.bubbleList));

        Intent intent = getIntent();

        setCont((ConversationLine) intent.getSerializableExtra("Contact"));
        this.setTitle(cont.getContactName());

        setBubbleData(Controller.loadMessages(this.getContentResolver(), getCont(), getCont().getMessageInTotal() - getCont().getNumberLoaded(), ConversationLine.LIMIT_LOAD_MESSAGE));

        getBubbleList().setStackFromBottom(true);
        getBubbleList().setDividerHeight(0);
        final ArrayBubbleAdapter adapter = new ArrayBubbleAdapter(this, getBubbleData());
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
}