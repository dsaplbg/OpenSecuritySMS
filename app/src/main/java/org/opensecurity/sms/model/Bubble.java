package org.opensecurity.sms.model;


public class Bubble {
    private String contenu;
    private String date;
    private boolean sendByMe;

    public Bubble(String contenu, String date, boolean sendByMe) {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isSendByMe() {
        return sendByMe;
    }

    public void setSendByMe(boolean sendByMe) {
        this.sendByMe = sendByMe;
    }
}
