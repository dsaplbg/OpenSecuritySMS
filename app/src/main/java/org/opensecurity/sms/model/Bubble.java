package org.opensecurity.sms.model;


import java.sql.Date;

public class Bubble {
    private String contenu;
    private Date date;
    private boolean sendByMe;

    public Bubble(String contenu, Date date, boolean sendByMe) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSendByMe() {
        return sendByMe;
    }

    public void setSendByMe(boolean sendByMe) {
        this.sendByMe = sendByMe;
    }
}
