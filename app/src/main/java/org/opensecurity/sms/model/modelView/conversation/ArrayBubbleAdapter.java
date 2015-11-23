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
 * Created by public on 04/10/15.
 */
public class ArrayBubbleAdapter extends ArrayAdapter {
    private View bubbleView;
    private ArrayList<ConversationItem> mBubbles;
    private LayoutInflater mLayoutInflater;

    static class ViewHolder {
        private TextView messageBody;
    }

    /**
     *
     * @param c
     * @param mb
     */
    public ArrayBubbleAdapter(Context c, ArrayList<ConversationItem> mb) {
        super(c, R.layout.bubble_item, mb);
        mBubbles = mb;
        mLayoutInflater = LayoutInflater.from(c);
    }

    @Override
    public int getViewTypeCount() {
        // menu type count
        return mBubbles.size();
    }

    @Override
    public int getItemViewType(int position) {
        // current menu type
        return position;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        bubbleView = convertView;
        if (convertView == null) {
            bubbleView = this.mLayoutInflater.inflate(R.layout.bubble_item, null);
            holder = new ViewHolder();
            holder.messageBody = (TextView) bubbleView.findViewById(R.id.b_contenu);

            bubbleView.setTag(holder);
        } else {
            holder = (ViewHolder) bubbleView.getTag();
        }

        ConversationItem item = mBubbles.get(position);

        /**
         * if it's an instance of Bubble, we create an item with a messageBody, imageBubble etc...
         * Else, We write the date.
         */
        if (item instanceof Bubble) {
            Bubble bubble = (Bubble) item;
            holder.messageBody.setText(bubble.getContenu());

            holder.messageBody.setMaxWidth((int) (parent.getWidth() * 0.9));
            LinearLayout layout = (LinearLayout) bubbleView.findViewById(R.id.layoutBubble);
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

        return bubbleView;
    }

}
