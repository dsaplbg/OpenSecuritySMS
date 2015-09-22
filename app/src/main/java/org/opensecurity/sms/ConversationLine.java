package org.opensecurity.sms;

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

    public ConversationLine(String contactName, String latestMessage){
        setContactName(contactName);
        setLatestMessage(latestMessage);
    }


    public void setLatestMessage(String latestMessage) {
        this.latestMessage = latestMessage;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public final String getContactName() {
        return this.contactName;
    }

    public final String getLatestMessage() {
        return this.latestMessage;
    }

}
