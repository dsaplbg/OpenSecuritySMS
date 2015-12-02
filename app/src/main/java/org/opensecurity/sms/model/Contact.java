package org.opensecurity.sms.model;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by Valentin on 10/11/2015.
 */
public class Contact implements Serializable {

    private String name, number, photoURL;
    private int threadId;  // There is only one thread for a contact so we can save it here

    public Contact(String number) {
        setName(number);
        setNumber(number);
        setPhotoURL(null);
        setThreadId(0);
    }

    public Contact(String name, String number, String photoURL, int threadId) {
        setName(name);
        setNumber(number);
        setPhotoURL(photoURL);
        setThreadId(threadId);
    }

    public final Bitmap getPhoto(ContentResolver contentResolver) {
        Bitmap b = null;

        if (hasPhoto()) {
            try {
                b = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(getPhotoURL()));
            } catch (IOException e) {
                Log.i("Photo error", e.getMessage());
            }
        }

        return (b != null ? b : createLetterPhoto());
    }

    private final Bitmap createLetterPhoto() {
        Bitmap b = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.drawColor(Color.DKGRAY);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.WHITE);

        if (getNumber().equals(getName())) {
            c.drawCircle(25, 15, 8, p);
            c.drawCircle(25, 48, 20, p);
            p.setColor(Color.DKGRAY);
            c.drawRect(0, 45, 50, 50, p);
        } else {
            String lettre = getName().substring(0, 1);

            p.setTextSize(35);
            p.setShadowLayer(1f, 0f, 1f, Color.BLACK);
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

    public boolean hasPhoto() {
        return this.photoURL != null && !this.photoURL.isEmpty();
    }


    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }
}
