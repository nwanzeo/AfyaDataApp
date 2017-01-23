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

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.sacids.afyadata.R;
import org.sacids.afyadata.models.FormDetails;

import java.util.List;

/**
 * Created by Renfrid-Sacids on 7/5/2016.
 */
public class FormDetailsAdapter extends BaseAdapter {
    private Activity activity;
    private List<FormDetails> formDetailsList;
    private LayoutInflater inflater;

    public FormDetailsAdapter(Activity activity, List<FormDetails> formsList) {
        this.activity = activity;
        this.formDetailsList = formsList;
    }

    @Override
    public int getCount() {
        return formDetailsList.size();
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
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_form_details, null);

        //TextView
        TextView label = (TextView) convertView.findViewById(R.id.label);
        TextView value = (TextView) convertView.findViewById(R.id.value);

        //Campaign Model
        FormDetails forms = formDetailsList.get(position);

        //set variables
        label.setText(forms.getLabel());
        value.setText(forms.getValue());


        return convertView;
    }

}
