package org.opensecurity.sms.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.view.menu.MenuView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.opensecurity.sms.R;
import org.opensecurity.sms.controller.Controller;
import org.opensecurity.sms.model.ArrayBubbleAdapter;
import org.opensecurity.sms.model.Bubble;
import org.opensecurity.sms.model.ConversationLine;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class ConversationActivity extends AppCompatActivity {
	private ArrayList<Bubble> bubbleData;
	private SwipeMenuListView bubbleList;
	private ConversationLine cont;

	private TextView contactName;
	private ImageView photoContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_conversation);

		setBubbleData(new ArrayList<Bubble>());
		setBubbleList((SwipeMenuListView) findViewById(R.id.bubbleList));

		Intent intent = getIntent();

		setCont((ConversationLine) intent.getSerializableExtra("Contact"));
		this.setTitle(cont.getContactName());

		setBubbleData(Controller.loadMessages(this.getContentResolver(), getCont()));

		getBubbleList().setStackFromBottom(true);
		getBubbleList().setDividerHeight(0);
		getBubbleList().setAdapter(new ArrayBubbleAdapter(this, getBubbleData()));

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
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
        };

        // set creator
        bubbleList.setMenuCreator(creator);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_conversation, menu);

		MenuItem photo = menu.getItem(0);
		if (cont.hasPhoto()) {
			try {
				InputStream inputStream = getContentResolver().openInputStream(Uri.parse(cont.getPhotoUrl()));
				Bitmap origin = BitmapFactory.decodeStream(inputStream);
				Bitmap b = Bitmap.createScaledBitmap(origin, 2 * origin.getWidth(), 2 * origin.getHeight(), true);
				photo.setIcon(new BitmapDrawable(getResources(), b));
			} catch (FileNotFoundException e) {
				photo.setTitle("Infos");
			}
		} else photo.setTitle("Infos");  //no contact photo

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

	public ArrayList<Bubble> getBubbleData() {
		return bubbleData;
	}

	public void setBubbleData(ArrayList<Bubble> bubbleData) {
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