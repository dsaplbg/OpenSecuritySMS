package org.opensecurity.sms.model.discussion;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Message contains every elements a message should have
 */
public class Message {

    /**
     * The message content. Currently String so won't support MMS !
     */
    private String content;

    /**
     * true if it's a message send by me. False if not
     */
    private boolean sendByMe;

    /**
     * The message date
     */
    private Calendar date;

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
    public Message(String content, Calendar date, boolean sendByMe) {
        setContent(content);
        setDate(date);
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
    public String getManagedDate() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(getDate().getTime());
    }

    /**
     * to know if we have to managed the date
     * @param date the date to compare
     * @return true if it's the same date as today
     */
    public boolean hasToManagedDate(Calendar date) {
        date = (Calendar) date.clone();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        Calendar dateMsg = (Calendar) getDate().clone();
        dateMsg.set(Calendar.HOUR_OF_DAY, 0);
        dateMsg.set(Calendar.MINUTE, 0);
        dateMsg.set(Calendar.SECOND, 0);
        dateMsg.set(Calendar.MILLISECOND, 0);

        return dateMsg.compareTo(date) != 0;
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

    /**
     * The date of a message
     */
    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }
}
