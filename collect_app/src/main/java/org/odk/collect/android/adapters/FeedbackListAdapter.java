package org.odk.collect.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import org.odk.collect.android.R;
import org.odk.collect.android.models.Feedback;

import java.util.List;


/**
 * Created by Renfrid-Sacids on 2/11/2016.
 */
public class FeedbackListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Feedback> feedbackList;

    public FeedbackListAdapter(Activity activity, List<Feedback> feedback) {
        this.activity = activity;
        this.feedbackList = feedback;
    }

    @Override
    public int getCount() {
        return feedbackList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {

            Feedback msg = feedbackList.get(position);

            //check user
            if (msg.getSender().equalsIgnoreCase("user")) {
                convertView = inflater.inflate(R.layout.feedback_item_right, null);
            } else {
                convertView = inflater.inflate(R.layout.feedback_item_left, null);
            }

            //get TextView
            TextView tv = (TextView) convertView.findViewById(R.id.tv_message);
            tv.setText(msg.getMessage());

        }
        return convertView;
    }
}

