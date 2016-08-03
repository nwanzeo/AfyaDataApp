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
