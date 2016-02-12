package org.opensecurity.sms.model.talk;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A class to keep a date of message provided from  one bubble.
 * A mother class from Bubble
 */
public class ConversationItem {

    /**
     * the date of a message
     */
    private Calendar date;

    /**
     * constructor
     * @param date the date of a message
     */
    public ConversationItem(Calendar date) {
        setDate(date);
    }

    /**
     * to get the date of a message
     * @return the date of a message
     */
    public Calendar getDate() {
        return date;
    }

    /**
     * to get the managed date (the format)
     * @return the date after formatted it
     */
    public String getManagedDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/y - HH:mm");
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
     * to set the date of the message
     * @param date the date of the message
     */
    public void setDate(Calendar date) {
        this.date = date;
    }
}
