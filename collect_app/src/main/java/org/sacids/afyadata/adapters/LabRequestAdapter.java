package org.sacids.afyadata.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.sacids.afyadata.R;
import org.sacids.afyadata.models.LabRequest;

import java.util.List;

/**
 * Created by Renfrid-Sacids on 10/5/2016.
 */
public class LabRequestAdapter extends BaseAdapter {
    private Activity activity;
    private List<LabRequest> labRequestList;
    private LayoutInflater inflater;

    public LabRequestAdapter(Activity activity, List<LabRequest> labRequestList) {
        this.activity = activity;
        this.labRequestList = labRequestList;
    }

    @Override
    public int getCount() {
        return labRequestList.size();
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
            convertView = inflater.inflate(R.layout.list_lab_request, null);

        //TextView
        TextView label = (TextView) convertView.findViewById(R.id.label);
        TextView value = (TextView) convertView.findViewById(R.id.value);

        //Lab
        LabRequest labRequest = labRequestList.get(position);

        //set variables
        label.setText(labRequest.getLabel());
        value.setText(labRequest.getValue());

        return convertView;
    }
}
