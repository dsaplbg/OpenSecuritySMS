package org.opensecurity.sms.model.modelView.conversation;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.opensecurity.sms.R;

import java.util.ArrayList;

/**
 * the adapter for displaing the listview of bubbles in a conversation activity
 */
public class ArrayBubbleAdapter extends ArrayAdapter {

    /**
     * the bubbles of our conversation in a arrayList
     */
    private ArrayList<ConversationItem> mBubbles;

    /**
     * to instantiates a layout XML file into its corresponding view objects
     */
    private LayoutInflater mLayoutInflater;

    /**
     * a static class to keep the view
     */
    static class ViewHolder {
        private TextView messageBody;
    }

    /**
     * constructor.
     * @param c context interface to global information about an application environment.
     * @param mb arrayList of bubbles
     */
    public ArrayBubbleAdapter(Context c, ArrayList<ConversationItem> mb) {
        super(c, R.layout.bubble_item, mb);
        mBubbles = mb;
        mLayoutInflater = LayoutInflater.from(c);
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    /**
     * to get the number of bubbles in our conversation activity
     * @return the number of bubbles
     */
    @Override
    public int getViewTypeCount() {
        // menu type count
        return mBubbles.size();
    }

    /**
     * to get the current menu type
     * @param position the position of selected item
     * @return the position of selected item
     */
    @Override
    public int getItemViewType(int position) {
        // current menu type
        return position;
    }

    /**
     * get a view that displays the data at the specified position in the data set.
     * This function is called for each rows
     * a bubble contains information loaded thanks to the controller
     *
     * @param position the position of the item in the listview
     * @param convertView the old view to rescue
     * @param parent the parent that this view will eventually be attached to (conversationActivity)
     * @return the view of a bubble. Created view by us
     */
    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View bubbleViewRow = convertView;

        if (convertView == null) {
            //create a new object with the same properties that the buuble_item
            bubbleViewRow = this.mLayoutInflater.inflate(R.layout.bubble_item, null);

            holder = new ViewHolder();
            holder.messageBody = (TextView) bubbleViewRow.findViewById(R.id.b_contenu);

            bubbleViewRow.setTag(holder);
        } else {
            holder = (ViewHolder) bubbleViewRow.getTag();
        }

        ConversationItem item = mBubbles.get(position);

        /**
         * if it's an instance of Bubble, we create an item with a messageBody, imageBubble etc...
         * Else, We write the date.
         */
        if (item instanceof Bubble) {
            Bubble bubble = (Bubble) item;
            holder.messageBody.setText(bubble.getContent());

            holder.messageBody.setMaxWidth((int) (parent.getWidth() * 0.9));
            LinearLayout layout = (LinearLayout) bubbleViewRow.findViewById(R.id.layoutBubble);
            if (bubble.isSendByMe()) {
                holder.messageBody.setBackgroundResource(R.drawable.bulle_me);
                layout.setGravity(Gravity.RIGHT);
            } else {
                holder.messageBody.setBackgroundResource(R.drawable.bulle_not_me);
                layout.setGravity(Gravity.LEFT);
            }
            holder.messageBody.setPadding(50, 50, 50, 50);
        } else {
            holder.messageBody.setTextSize(13);
            holder.messageBody.setText(item.getManagedDate());
        }

        return bubbleViewRow;
    }
}
