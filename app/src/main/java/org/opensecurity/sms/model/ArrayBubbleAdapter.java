package org.opensecurity.sms.model;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.opensecurity.sms.R;

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
        private TextView messageDate;
    }
    public ArrayBubbleAdapter(Context c, ArrayList<Bubble> mb) {
        super(c, R.layout.bubble_list, mb);
        mBubbles = mb;
        mLayoutInflater = LayoutInflater.from(c);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        bubbleView = convertView;
        if (convertView == null) {
            bubbleView = this.mLayoutInflater.inflate(R.layout.bubble_list, null);
            holder = new ViewHolder();
            holder.messageBody = (TextView) bubbleView.findViewById(R.id.b_contenu);
            holder.messageDate = (TextView) bubbleView.findViewById(R.id.b_date);

            bubbleView.setTag(holder);
        } else {
            holder = (ViewHolder) bubbleView.getTag();
        }
        Color c = new Color();
        if(mBubbles.get(position).isSendByMe()) {
            bubbleView.setBackgroundColor(c.argb(100, 150,170,180));
        } else {
            bubbleView.setBackgroundColor(c.argb(100, 200,200,150));
        }


        Bubble bubble = mBubbles.get(position);
        holder.messageDate.setText(bubble.getDate().toString());
        holder.messageBody.setText(bubble.getContenu());

        return bubbleView;
    }

}
