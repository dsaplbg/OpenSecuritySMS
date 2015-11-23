package org.opensecurity.sms.model.modelView.conversation;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A class to keep a date of message provided from  one bubble.
 * A mother class from Bubble
 */
public class ConversationItem {

    private Calendar date;

    /**
     *
     * @param date
     */
    public ConversationItem(Calendar date) {
        setDate(date);
    }

    public Calendar getDate() {
        return date;
    }

    /**
     *
     * @return
     */
    public String getManagedDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/y - HH:mm");
        return format.format(getDate().getTime());
    }

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

    public void setDate(Calendar date) {
        this.date = date;
    }
}
