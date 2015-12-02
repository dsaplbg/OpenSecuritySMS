package org.opensecurity.sms.view;

import android.app.Activity;
import android.os.Bundle;
import org.opensecurity.sms.R;
/**
 * Created by Valentin on 11/11/2015.
 */
public class PopupConversationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.popup_conversation);



        listeners();
    }

    /**
     * method used to initialization of all listeners.
     * 1st listener : sendButton onClick.
     *
     */
    public void listeners() {

    }
}
