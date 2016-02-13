package org.opensecurity.sms.model.discussion;

import android.content.ContentResolver;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.opensecurity.sms.R;

import java.util.List;

/**
 * This class is used to set an adapter of a list of conversation (in the main activity)
 * it's a child of ArrayAdapter.
 */
public class ArrayDiscussionAdapter extends ArrayAdapter {

    private LayoutInflater mLayoutInflater;
    private ContentResolver contentResolver;

    static class ViewHolder {
        public TextView name;
        public TextView latestCon;
        public TextView date;
        public ImageView photo;
    }

    /**
     *
     * @param context
     * @param rep
     */
    public ArrayDiscussionAdapter(Context context, List<Message> rep){
        super(context, R.layout.list_of_convers, rep);
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
     * will be composed of, thanks to XML, tow entities of the TextView. (ref to R.layout.list_of_conversrs.xml)
     *this method is called every time the program need to generate a rowview.
     *
     * If you want to improve the design of the listOfConvers activity (main activity) refer to
     * R.layout.list_of_convers.xmlml and R.layout.opensecuritysms.xml
     */
    /**
     * get a view that displays the data at the specified position in the data set.
     * This function is called for each rows
     * a row contains information loaded thanks to the controller
     *
     * @param position the position of the item in the listview
     * @param convertView the old view to rescue
     * @param parent the parent that this view will eventually be attached to (mainActivity)
     * @return the view of a row. Created view by us
     */
    @Override
    public View getView (int position, View convertView, ViewGroup parent) {

        ViewHolder holder;  //use this class to keep element when one of them disappear (by scrolling for example)
        View rowView = convertView;

        //if the parameter convertView is null, we have to recalculate rowview thanks to the following lines.
        if(rowView == null) {
            //la méthode inflate permet de créer un objet view à partir d'un xml.
            //inflate method permit to create a View object since the xml
            rowView = this.mLayoutInflater.inflate(R.layout.list_of_convers, null);

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

        Message convers = (Message) getItem(position);
        holder.photo.setImageBitmap(convers.getContact().getPhoto(contentResolver));
        holder.name.setText(convers.getContact().getName());
        holder.latestCon.setText(convers.getContent());
        holder.date.setText(convers.getManagedDate());

        holder.latestCon.setPadding(5,10,0,15);
        return rowView;
    }
}
