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

    /**
     * the limit of messages can be loaded
     */
    public static int LIMIT_LOAD_MESSAGE = 20;

    /**
     * the latest message
     */
    private String latestMessage;

    /**
     * the date of latest message
     */
    private Calendar date;

    /**
     * the primary key of the contact
     */
    private int thread_ID;

    /**
     * the contact of the row
     */
    private Contact contact;

    /**
     * Constructor
     *
     * @param contact is the contact of the row
     * @param latestMessage is the latestMessage between you and he
     * @param date the date of latest message
     */
    public ConversationLine(Contact contact, String latestMessage, Calendar date){
        setContact(contact);
        setLatestMessage(latestMessage);
        setDate(date);
        setThread_id(contact.getThreadId());
    }

    /**
     * to get the threadId of the contact in the row
     * @return the threadId of the contact in the row
     */
    public int getThread_ID() {
        return thread_ID;
    }

    /**
     * to set the threadId of the contact in the row
     * @param ID the id of the contact in the row
     */
    public void setThread_id(int ID) {
        this.thread_ID = ID;
    }

    /**
     * to set the latestMessage for a row.
     * @param latestMessage the lastest message for a row
     */
    public void setLatestMessage(String latestMessage) {
        latestMessage = (latestMessage.length() > 100)?(latestMessage.substring(0, 97) + "..."):latestMessage;
        this.latestMessage = latestMessage;
    }

    /**
     * to set the contact of a row
     * @param contact the contact of a row
     */
    public void setContact(Contact contact) {
        this.contact = contact;
    }

    /**
     * to set the date of a row
     * @param date the date of a row
     */
    public void setDate(Calendar date) {
        this.date = date;
    }

    /**
     * to get the latest message of a row
     * @return the latestmessage of a row
     */
    public final String getLatestMessage() {
        return this.latestMessage;
    }

    /**
     * to get the date of a row
     * @return the date of a row
     */
    public  final Calendar getDate() {
        return this.date;
    }

    /**
     * to get the managed date of a row
     * @return the managedDate of a row
     */
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

    /**
     * to get the contact of a row
     * @return the contact of a row
     */
    public Contact getContact() {
        return contact;
    }
}