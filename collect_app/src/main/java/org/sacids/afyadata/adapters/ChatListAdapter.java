/*
 * Copyright (C) 2016 Sacids Tanzania
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.sacids.afyadata.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import org.sacids.afyadata.R;
import org.sacids.afyadata.models.Feedback;
import org.sacids.afyadata.preferences.PreferencesActivity;

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
        tvMessage.setText(Html.fromHtml(feedback.getMessage()));

        return convertView;
    }
}

