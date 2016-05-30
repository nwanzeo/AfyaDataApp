package org.odk.collect.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.android.models.Disease;

import java.util.List;

/**
 * Created by Renfrid-Sacids on 5/30/2016.
 */
public class DiseaseListAdapter extends BaseAdapter {
    private Activity activity;
    private List<Disease> diseaseList;
    private LayoutInflater inflater;

    public DiseaseListAdapter(Activity activity, List<Disease> diseases) {
        this.activity = activity;
        this.diseaseList = diseases;
    }

    @Override
    public int getCount() {
        return diseaseList.size();
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
            convertView = inflater.inflate(R.layout.list_row_disease, null);

        //TextView
        TextView name = (TextView) convertView.findViewById(R.id.disease_name);

        //Campaign Model
        Disease disease = diseaseList.get(position);

        name.setText(disease.getTitle());

        return convertView;
    }

}

