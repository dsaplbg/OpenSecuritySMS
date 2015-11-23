package org.opensecurity.sms.model.modelView.conversation;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Bubble extends ConversationItem {
    private String contenu;
    private boolean sendByMe;

    /**
     *
     * @param contenu
     * @param date
     * @param sendByMe
     */
    public Bubble(String contenu, Calendar date, boolean sendByMe) {
        super(date);
        setContenu(contenu);
        setSendByMe(sendByMe);
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    /**
     * Use it to write the date. Called from itemSwitch from ConversationActivity.
     * Son function just to write hours and minuts.
     * @return
     */
    @Override
    public String getManagedDate() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(getDate().getTime());
    }

    public boolean isSendByMe() {
        return sendByMe;
    }

    public void setSendByMe(boolean sendByMe) {
        this.sendByMe = sendByMe;
    }
}
