package org.opensecurity.sms.model.modelView.conversation;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * class bubble. On of conversationItem. Possible row in a conversationActivity.
 */
public class Bubble extends ConversationItem {

    /**
     * the text into a conversationItem
     */
    private String content;

    /**
     * true if it's a message send by me. False if not
     */
    private boolean sendByMe;

    /**
     * Constructor
     *
     * @param content
     *          The content of the bubble
     * @param date
     *          The date of reception of the message
     * @param sendByMe
     *          True if send by the phone, False if not
     */
    public Bubble(String content, Calendar date, boolean sendByMe) {
        super(date);
        setContent(content);
        setSendByMe(sendByMe);
    }

    /**
     * This method returns the content of the bubble
     *
     * @return
     *          The content of the bubble
     */
    public String getContent() {
        return content;
    }

    /**
     * This method set the content of the bubble
     *
     * @param content
     *          The content of the bubble
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * This method computes the date in a human readable format
     * using hours and minutes.
     *
     * @return
     *          The human readable date
     */
    @Override
    public String getManagedDate() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(getDate().getTime());
    }

    /**
     * This method returns True if the message was sent by the phone,
     * False if not
     *
     * @return
     *          True if the message was sent by the phone,
     *          False if not
     */
    public boolean isSendByMe() {
        return sendByMe;
    }

    /**
     * This method sets the boolean sendByMe
     *
     * @param sendByMe
     *          The message status
     */
    public void setSendByMe(boolean sendByMe) {
        this.sendByMe = sendByMe;
    }
}
