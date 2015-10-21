package org.opensecurity.sms.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.SimpleTimeZone;

public class Bubble {
    private String contenu;
    private Calendar date;
    private boolean sendByMe;

    public Bubble(String contenu, Calendar date, boolean sendByMe) {
        setContenu(contenu);
        setDate(date);
        setSendByMe(sendByMe);
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Calendar getDate() {
        return date;
    }

    public String getManagedDate() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(getDate().getTime());
    }


    public void setDate(Calendar date) {
        this.date = date;
    }

    public boolean isSendByMe() {
        return sendByMe;
    }

    public void setSendByMe(boolean sendByMe) {
        this.sendByMe = sendByMe;
    }
}
