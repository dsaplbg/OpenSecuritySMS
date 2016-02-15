package org.opensecurity.sms.model.database;

import android.content.Context;

/**
 * Created by hanriaca on 15/02/16.
 */
public class MessageDAO {
    private Context currentContex;

    public MessageDAO(Context currentContex) {
        this.currentContex = currentContex;
    }

    public Context getCurrentContex() {
        return currentContex;
    }

    public void setCurrentContex(Context currentContex) {
        this.currentContex = currentContex;
    }
}
