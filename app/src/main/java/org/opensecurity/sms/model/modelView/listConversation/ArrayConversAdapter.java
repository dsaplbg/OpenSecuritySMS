package org.opensecurity.sms.model.modelView.listConversation;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.opensecurity.sms.R;

import java.util.List;

/**
 * Created by root on 19/09/15.
 *
 * This class is used to set an adapter of a list of conversation (in the main activity)
 * it's a child of ArrayAdapter.
 */
public class ArrayConversAdapter extends ArrayAdapter {
    private List<ConversationLine> mRepertoire;
    private LayoutInflater mLayoutInflater;
    private View rowView;
    private ContentResolver contentResolver;

    static class ViewHolder {
        public TextView name;
        public TextView latestCon;
        public TextView date;
        public ImageView photo;

    }
    public ArrayConversAdapter(Context context, List<ConversationLine> rep){
        super(context, R.layout.listofconvers, rep);
        this.mRepertoire = rep;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.contentResolver = context.getContentResolver();
    }


    /**
     * position : position in the list of  conversations..
     * convertView : given by the system to recycle or recalculate rowView wich  has just appeared (by scrolling for example)
     *
     * permet la récupération de la vue personnalisée d'une ligne (rowView) qui
     * contiendra, grâce au XML, nos deux élements de textes que nous pourront
     * exploiter afin de pouvoir y personnaliser. La méthode getView est appelée
     * pour générer chaque lignes de l'écran.
     *
     * This method is a redefinition of getView in class ArrayAdapter witch is used for
     * calculate one element of our list of widget rowView created by us.
     * permit the recycling (or calculate) and return the personalized view of a line (rowView) witch
     * will be composed of, thanks to XML, tow entities of the TextView. (ref to R.layout.listofconvers.xml)
     *this method is called every time the program need to generate a rowview.
     *
     * If you want to improve the design of the listOfConvers activity (main activity) refer to
     * R.layout.listofconvers.xml and R.layout.opensecuritysms.xml
     */
    @Override
    public View getView (int position, View convertView, ViewGroup parent) {

        ViewHolder holder;  //use this class to keep element when one of them disappear (by scrolling for example)
        rowView = convertView;

        //if the parameter convertView is null, we have to recalculate rowview thanks to the following lines.
        if(convertView == null) {
            //la méthode inflate permet de créer un objet view à partir d'un xml.
            //inflate method permit to create a View object since the xml
            rowView = this.mLayoutInflater.inflate(R.layout.listofconvers, null);

            /*initialization of holder, because convertView is null so holder couldn't know
            members assignment. We have to recreate it with the following lines*/
            holder = new ViewHolder();
            holder.photo = (ImageView) rowView.findViewById(R.id.photoContact);
            holder.name = (TextView) rowView.findViewById(R.id.nameContact);
            holder.latestCon = (TextView) rowView.findViewById(R.id.latestMessage);
            holder.date = (TextView) rowView.findViewById(R.id.date);
            rowView.setTag(holder);
        }
        //else, the parameter convertView exists so the element can be save with the convertView passed by the system
        else {
            holder = (ViewHolder) rowView.getTag();
        }

        //Color c = new Color();
        //rowView.setBackgroundColor(c.argb(50,250,250,190));

        ConversationLine convers = mRepertoire.get(position);
        holder.photo.setImageBitmap(convers.getContact().getPhoto(contentResolver));
        holder.name.setText(convers.getContact().getName());
        holder.latestCon.setText(convers.getLatestMessage());
        holder.date.setText(convers.getManagedDate());

        holder.latestCon.setPadding(5,10,0,15);
        return rowView;
    }
}
