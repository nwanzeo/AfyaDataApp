package org.odk.collect.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import org.odk.collect.android.R;
import org.odk.collect.android.models.Feedback;
import org.odk.collect.android.preferences.PreferencesActivity;

import java.util.List;


/**
 * Created by Renfrid-Sacids on 2/11/2016.
 */
public class ChatListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Feedback> feedbackList;
    private SharedPreferences mSharedPreferences;
    private String username;

    public ChatListAdapter(Context context, List<Feedback> feedback) {
        this.context = context;
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
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        username = mSharedPreferences.getString(PreferencesActivity.KEY_USERNAME, null);


        Feedback feedback = feedbackList.get(position);

        if(feedback.getReplyBy().equals("0") && feedback.getUserName().equals(username)){
            convertView = li.inflate(R.layout.feedback_item_right, null);
        }
        else{
            convertView = li.inflate(R.layout.feedback_item_left, null);
        }

        TextView tvMessage = (TextView) convertView.findViewById(R.id.tvMessage);
        tvMessage.setText(feedback.getMessage());

        return convertView;
    }
}

