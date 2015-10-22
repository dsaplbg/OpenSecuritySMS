package org.opensecurity.sms.model.modelView.listConversation;

import android.util.Log;

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

    private String contactName;
    private String latestMessage;
    private Calendar date;
    private int thread_ID;
    private String photoUrl;
    private String number;
    private int numberMessagesInTotal, numberLoaded;
    private boolean reloaded;

    public ConversationLine(String contactName, String latestMessage, Calendar date, int th_id, String photoUrl, String number, int numberMessagesInTotal){
        setContactName(contactName);
        setLatestMessage(latestMessage);
        setDate(date);
        setThread_id(th_id);
        setPhotoUrl(photoUrl);
        setNumber(number);
        setNumberMessagesInTotal(numberMessagesInTotal);
        setNumerLoaded(LIMIT_LOAD_MESSAGE);
        setReloaded(false);
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

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public final String getContactName() {
        return this.contactName;
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

    public final String getPhotoUrl() {
        return this.photoUrl;
    }

    public String getNumber() {
        return number;
    }

    public boolean hasPhoto() {
        return this.photoUrl != null && !this.photoUrl.isEmpty();
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

    public boolean isReloaded() {
        return reloaded;
    }

    public void setReloaded(boolean reloaded) {
        this.reloaded = reloaded;
    }
}