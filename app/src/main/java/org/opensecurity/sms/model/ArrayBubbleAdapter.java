package org.opensecurity.sms.model;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.opensecurity.sms.R;
import org.opensecurity.sms.view.ConversationActivity;
import org.opensecurity.sms.view.OpenSecuritySMS;

import java.util.ArrayList;

/**
 * Created by public on 04/10/15.
 */
public class ArrayBubbleAdapter extends ArrayAdapter {
    private View bubbleView;
    private ArrayList<Bubble> mBubbles;
    private LayoutInflater mLayoutInflater;

    static class ViewHolder {
        private TextView messageBody;
    }
    public ArrayBubbleAdapter(Context c, ArrayList<Bubble> mb) {
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

        Bubble bubble = mBubbles.get(position);
        holder.messageBody.setText(bubble.getContenu());

        holder.messageBody.setMaxWidth((int) (parent.getWidth() * 0.9));
        LinearLayout layout = (LinearLayout) bubbleView.findViewById(R.id.layoutBubble);
        if(mBubbles.get(position).isSendByMe()) {
            holder.messageBody.setBackgroundResource(R.drawable.bulle_me);
            layout.setGravity(Gravity.RIGHT);
        } else {
            holder.messageBody.setBackgroundResource(R.drawable.bulle_not_me);
            layout.setGravity(Gravity.LEFT);
        }
        holder.messageBody.setPadding(50,50,50,50);

        return bubbleView;
    }

}
