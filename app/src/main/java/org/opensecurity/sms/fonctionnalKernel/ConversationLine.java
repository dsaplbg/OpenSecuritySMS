package org.opensecurity.sms.fonctionnalKernel;

/**
 * Object used to show a line of conversation
 * in the main activity
 * A ConversationLine is a class which just implements
 * the contents of a rowView.
 * latestMessage will be the last message so display in the rowView
 * contactName will be the name of a Contact in the rowView
 */
public class ConversationLine {
    private String contactName;
    private String latestMessage;
    private String date;

    public ConversationLine(String contactName, String latestMessage, String date){
        setContactName(contactName);
        setLatestMessage(latestMessage);
        setDate(date);
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