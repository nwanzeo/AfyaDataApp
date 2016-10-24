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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.sacids.afyadata.R;
import org.sacids.afyadata.models.Feedback;

import java.util.List;

/**
 * Created by Renfrid-Sacids on 2/22/2016.
 */

public class FeedbackListAdapter extends BaseAdapter {
    private Context context;
    private List<Feedback> feedbackList;

    public FeedbackListAdapter(Context context, List<Feedback> feedbackList) {
        this.context = context;
        this.feedbackList = feedbackList;
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
        convertView = li.inflate(R.layout.list_item_feedback, null);

        Feedback feedback = feedbackList.get(position);

        TextView form_name = (TextView) convertView.findViewById(R.id.form_name);
        form_name.setText(feedback.getTitle());

        TextView last_message = (TextView) convertView.findViewById(R.id.last_message);
        last_message.setText(Html.fromHtml(feedback.getMessage()));

        TextView chr_name = (TextView) convertView.findViewById(R.id.chr_name);
        chr_name.setText(feedback.getChrName());

        TextView instance_id = (TextView) convertView.findViewById(R.id.instance_id);
        instance_id.setText(feedback.getInstanceId());

        TextView form_id = (TextView) convertView.findViewById(R.id.form_id);
        form_id.setText(feedback.getFormId());

        return convertView;
    }
}
