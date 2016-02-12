package org.opensecurity.sms.model.discussion;

import org.opensecurity.sms.model.Contact;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Message contains every elements a message should have
 */
public class Message implements Serializable{

    /**
     * The message content. Currently String so won't support MMS !
     */
    private String content;

    /**
     * true if it's a message send by me. False if not
     */
    private boolean sendByMe;

    /**
     * The contact who wrote the message
     */
    private Contact contact;

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
     * @param contact
     *          The contact who wrote the message
     * @param sendByMe
     *          True if send by the phone, False if not
     */
    public Message(String content, Calendar date, Contact contact, boolean sendByMe) {
        setContent(content);
        setDate(date);
        setContact(contact);
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

    /**
     * Contact who wrote the message
     */
    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
