package org.opensecurity.sms.model;

import java.io.Serializable;

/**
 * Object used to show a line of conversation
 * in the main activity
 * A ConversationLine is a class which just implements
 * the contents of a rowView.
 * latestMessage will be the last message so display in the rowView
 * contactName will be the name of a Contact in the rowView
 */
public class ConversationLine implements Serializable {
    private String contactName;
    private String latestMessage;
    private String date;
    private String thread_ID;

    public ConversationLine(String contactName, String latestMessage, String date, String th_id){
        setContactName(contactName);
        setLatestMessage(latestMessage);
        setDate(date);
        setThread_id(th_id);
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

    public final String getContactName() {
        return this.contactName;
    }

    public final String getLatestMessage() {
        return this.latestMessage;
    }

    public  final String getDate() {
        return this.date;
    }
}