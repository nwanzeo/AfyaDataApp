package org.odk.collect.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.android.models.SurveyForm;

import java.util.List;

/**
 * Created by Renfrid-Sacids on 2/22/2016.
 */

public class SurveyFormListAdapter extends BaseAdapter {
    private Context context;
    private List<SurveyForm> formList;

    public SurveyFormListAdapter(Context context, List<SurveyForm> myFormList) {
        this.context = context;
        this.formList = myFormList;
    }

    @Override
    public int getCount() {
        return formList.size();
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
        View view = li.inflate(R.layout.single_form, null);

        SurveyForm mForm = formList.get(position);

        TextView tv = (TextView) view.findViewById(R.id.tv_form_name);
        tv.setText(mForm.getDisplayName());

        tv = (TextView) view.findViewById(R.id.tv_form_short_description);
        tv.setText(mForm.getDisplaySubText());

        tv = (TextView) view.findViewById(R.id.tv_form_status);
        tv.setText(mForm.getStatus());

        tv = (TextView) view.findViewById(R.id.instance_id);
        tv.setText(mForm.getJrInstanceId());

        tv = (TextView) view.findViewById(R.id.form_id);
        tv.setText(mForm.getJrFormId());

        return view;
    }
}
