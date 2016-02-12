package org.opensecurity.sms.activities;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.opensecurity.sms.R;
import org.opensecurity.sms.model.Engine;
import org.opensecurity.sms.model.Contact;

/**
 * Created by Valentin on 11/11/2015.
 * The popup when we receive a sms.
 */
public class PopupConversationActivity extends Activity implements GestureDetector.OnGestureListener {

    /**
     *
     */
    private GestureDetectorCompat mDetector;

    private Engine engine;
    /**
     * the contact who send an sms
     */
    private Contact contact;

    /**
     * the contains of sms
     */
    private TextView contactText, messageText;
    private Button buttonSend;
    private ImageButton photoContact;
    private EditText textSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpWindow();
        this.setContentView(R.layout.popup_conversation);

        this.messageText = (TextView) findViewById(R.id.popupMessage);
        this.buttonSend = (Button) findViewById(R.id.popupSend);
        this.contactText = (TextView) findViewById(R.id.popupContact);
        this.photoContact = (ImageButton) findViewById(R.id.popupContactImage);
        this.textSend = (EditText) findViewById(R.id.popupTextSend);

        this.messageText.setText(((String) getIntent().getExtras().get("Message")));
        setContact(((Contact) getIntent().getExtras().get("Contact")));

        setEngine(new Engine(this.getApplicationContext()));
        listeners();
    }

    /**
     * method used to initialization of all listeners.
     * 1st listener : sendButton onClick.
     *
     */
    public void listeners() {
        this.mDetector = new GestureDetectorCompat(this, this);

        this.buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textSend.getText().length() > 0 && getEngine().sendSMS(getBaseContext(), getContact(), textSend.getText().toString())) {
                    finish();
                }
            }
        });
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
        this.contactText.setText(contact.getName());
        this.photoContact.setImageBitmap(contact.getPhoto(getContentResolver()));
    }


    public void setUpWindow() {
        // Creates the layout for the window and the look of it
        requestWindowFeature(Window.FEATURE_ACTION_BAR);

        // Params for the window.
        // You can easily set the alpha and the dim behind the window from here
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 1.0f;    // lower than one makes it more transparent
        params.dimAmount = 0f;  // set it higher if you want to dim behind the window
        getWindow().setAttributes(params);

        // Gets the display size so that you can set the window to a percent of that
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        // You could also easily used an integer value from the shared preferences to set the percent
        if (height > width) {
            getWindow().setLayout((int) (width * .9), (int) (height * .7));
        } else {
            getWindow().setLayout((int) (width * .7), (int) (height * .8));
        }
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //System.out.println(velocityX + "  " + velocityY);
        if (velocityX > 2000 && velocityY < velocityX) this.finish();

        return true;
    }
}
