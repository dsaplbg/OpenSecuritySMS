package org.opensecurity.sms.model.modelView.listConversation;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import org.opensecurity.sms.R;
import org.opensecurity.sms.model.Contact;
import org.opensecurity.sms.view.ConversationActivity;
import org.opensecurity.sms.view.OpenSecuritySMS;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Object used to show a line of conversation
 * in the main activity
 * A ConversationLine is a class which just implements
 * the contents of a rowView.
 * latestMessage will be the last message so display in the rowView
 * contactName will be the name of a Contact in the rowView
 */
public class ConversationLine implements Serializable {

    public static int LIMIT_LOAD_MESSAGE = 20;

    private String latestMessage;
    private Calendar date;
    private int thread_ID;
    private Contact contact;
    private int numberMessagesInTotal, numberLoaded;

    public ConversationLine(Contact contact, String latestMessage, Calendar date, int numberMessagesInTotal){
        setContact(contact);
        setLatestMessage(latestMessage);
        setDate(date);
        setThread_id(contact.getThreadId());
        setNumberMessagesInTotal(numberMessagesInTotal);
        setNumerLoaded(LIMIT_LOAD_MESSAGE);
    }

    public int getThread_ID() {
        return thread_ID;
    }

    public void setThread_id(int ID) {
        this.thread_ID = ID;
    }

    public void setLatestMessage(String latestMessage) {
        latestMessage = (latestMessage.length() > 100)?(latestMessage.substring(0, 97) + "..."):latestMessage;
        this.latestMessage = latestMessage;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public final String getLatestMessage() {
        return this.latestMessage;
    }

    public  final Calendar getDate() {
        return this.date;
    }

    public String getManagedDate() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar dayWeek = (Calendar) today.clone();
        dayWeek.set(Calendar.DAY_OF_WEEK, dayWeek.getFirstDayOfWeek());

        Calendar dateMsg = (Calendar) getDate().clone();
        dateMsg.set(Calendar.HOUR_OF_DAY, 0);
        dateMsg.set(Calendar.MINUTE, 0);
        dateMsg.set(Calendar.SECOND, 0);
        dateMsg.set(Calendar.MILLISECOND, 0);

        if (dateMsg.compareTo(today) == 0) {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            return format.format(getDate().getTime());
        } else if (dateMsg.compareTo(dayWeek) >= 0) {
            SimpleDateFormat format = new SimpleDateFormat("E");
            return format.format(getDate().getTime());
        } else {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/y");
            return format.format(getDate().getTime());
        }
    }

    public void setNumberMessagesInTotal(int numberMessagesInTotal) {
        this.numberMessagesInTotal = numberMessagesInTotal;
    }

    public int getMessageInTotal() {
        return this.numberMessagesInTotal;
    }

    public void setNumerLoaded(int numerLoaded) {
        this.numberLoaded = numerLoaded;
    }

    public int getNumberLoaded() {
        return this.numberLoaded;
    }

    public Contact getContact() {
        return contact;
    }
}