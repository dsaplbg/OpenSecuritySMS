package org.opensecurity.sms.model;

import java.io.Serializable;

/**
 * Object used to show a line of conversation
 * in the main activity
 * A ConversationLine is a class which just implements
 * the contents of a rowView.
 * latestMessage will be the last message so display in the rowView
 * contactName will be the name of a Contact in the rowView
 * date : the date of one message
 * number : the phone number of the contact
 * photoUrl is the picture, encoded in string
 */
public class ConversationLine implements Serializable {
    private String contactName;
    private String latestMessage;
    private String date;
    private String thread_ID;
    private String photoUrl;
    private String number;

    public ConversationLine(String contactName, String latestMessage, String date, String th_id, String photoUrl, String number){
        setContactName(contactName);
        setLatestMessage(latestMessage);
        setDate(date);
        setThread_id(th_id);
        setPhotoUrl(photoUrl);
        setNumber(number);
    }

    public String getThread_ID() {
        return thread_ID;
    }

    public void setThread_id(String ID) {
        this.thread_ID = ID;
    }

    public void setLatestMessage(String latestMessage) {
        latestMessage = (latestMessage.length() > 100)?(latestMessage.substring(0, 97) + "..."):latestMessage;
        this.latestMessage = latestMessage;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setDate(String date) {
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

    public  final String getDate() {
        return this.date;
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
}