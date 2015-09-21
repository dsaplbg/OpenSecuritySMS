package org.opensecurity.sms;

/**
 * Created by root on 19/09/15.
 * object used to show a line of conversation
 * in the main activity

 */
public class ConversationLine {
    private String contactName;
    private String latestMessage;

    public ConversationLine(String contactName, String latestMessage){
        this.contactName = contactName;
        this.latestMessage = latestMessage;
    }


    public void setLatestMessage(String latestMessage) {
        this.latestMessage = latestMessage;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactName() {
        return contactName;
    }

    public String getLatestMessage() {
        return latestMessage;
    }


}
