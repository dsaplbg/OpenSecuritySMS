package org.opensecurity.sms.model;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import org.opensecurity.sms.model.discussion.Message;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Valentin
 * @author Colas Broux
 */
public class Contact implements Serializable {

    /**
     * name is the name of contact, phoneNumber is his phoneNumber and photoUrl is his picture contact
     */
    private String name,
            phoneNumber,
            photoURL;

    /**
     * id is like a primary key for on contact in database
     */
    private int id,
            nbMessages;

    private ArrayList<Message> messages;

    /**
     * constructor
     * @param phoneNumber his phoneNumber
     */
    public Contact(String phoneNumber) {
        setPhoneNumber(phoneNumber);
        setNbMessages(0);
    }

    public final Bitmap roundBitmap(Bitmap b) {
        Bitmap imageRounded = Bitmap.createBitmap(b.getWidth(), b.getHeight(), b.getConfig());
        Canvas canvas = new Canvas(imageRounded);
        Paint mpaint = new Paint();
        mpaint.setAntiAlias(true);
        mpaint.setShader(new BitmapShader(b, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect((new RectF(0, 0, b.getWidth(), b.getHeight())), 100, 100, mpaint);// Round Image Corner 100 100 100 100

        return imageRounded;
    }

    /**
     * To get his photo
     * @param contentResolver to manage access to a structured set of data in your phone
     * @return bitmap picture of our contact
     */
    public final Bitmap getPhoto(ContentResolver contentResolver) {
        Bitmap b = null;

        if (hasPhoto()) {
            try {
                b = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(getPhotoURL()));
            } catch (IOException e) {
                Log.i("Photo error", e.getMessage());
            }
        }

        return (b != null ? roundBitmap(b) : roundBitmap(createLetterPhoto()));
    }

    /**
     * to create letter on a picture if we havn't the picture for our contact
     * @return bitmap picture of our contact
     */
    private final Bitmap createLetterPhoto() {
        Bitmap b = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        Paint p = new Paint();
        p.setFlags(Paint.ANTI_ALIAS_FLAG);
        p.setAntiAlias(true);
        p.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));
        p.setColor(Color.WHITE);
        c.drawColor(Color.DKGRAY);


        if (getName() == null) {
            c.drawCircle(25, 15, 8, p);
            c.drawCircle(25, 48, 20, p);
            p.setColor(Color.DKGRAY);
            c.drawRect(0, 45, 50, 50, p);
        } else {
            String lettre = getName().substring(0, 1);

            p.setTextSize(35);
            p.setTextAlign(Paint.Align.LEFT);

            // draw text to the Canvas center
            Rect bounds = new Rect();
            p.getTextBounds(lettre, 0, lettre.length(), bounds);
            int x = c.getClipBounds().width() / 2 - bounds.width() / 2 - bounds.left;
            int y = c.getClipBounds().height() / 2 + bounds.height() / 2 - bounds.bottom;

            c.drawText(lettre, x, y, p);
        }

        return b;
    }

    @Override
    public String toString(){
        return "Name : " + getName()
                + "\nPhone number : " + getPhoneNumber()
                + "\nID : " + getId();
    }

    /**
     * to know if a contact has a picture
     * @return true if contact has a picture in database
     */
    public boolean hasPhoto() {
        return this.photoURL != null && !this.photoURL.isEmpty();
    }


    /**
     * to get his photoUrl (in database)
     * @return the photoUrl of current contact
     */
    public String getPhotoURL() {
        return photoURL;
    }

    /**
     * to set his photoUrl (in database)
     * @param  photoURL photoUrl of current contact
     */
    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    /**
     * to get the phoneNumber of our contact
     * @return the phoneNumber of our instance of contact
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * to set the phoneNumber of our contact
     * @param phoneNumber the phoneNumber in database
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * to get the name of current contact
     * @return the name of the current contact
     */
    public String getName() {
        return name;
    }

    /**
     * to set the name of a current contact
     * @param name his name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * to get the primary key of current contact
     * @return the id of one contact
     */
    public int getId() {
        return id;
    }

    /**
     * to set the id of one contact
     * @param id the id for the current contact
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * to get the numbers of messages between us and our contact
     * @return the phoneNumber of messages between us and our contact
     */
    public int getNbMessages() {
        return nbMessages;
    }

    /**
     * to set the phoneNumber of messages between us and our contact
     * @param nbMessages the phoneNumber of messages between us and our contact
     */
    public void setNbMessages(int nbMessages) {
        this.nbMessages = nbMessages;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
