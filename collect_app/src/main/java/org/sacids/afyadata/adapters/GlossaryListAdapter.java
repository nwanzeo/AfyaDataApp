package org.sacids.afyadata.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.sacids.afyadata.R;
import org.sacids.afyadata.models.Glossary;

import java.util.List;

/**
 * Created by Renfrid-Sacids on 6/23/2016.
 */
public class GlossaryListAdapter extends BaseAdapter {
    private Activity activity;
    private List<Glossary> glossaryList;
    private LayoutInflater inflater;

    public GlossaryListAdapter(Activity activity, List<Glossary> glossary) {
        this.activity = activity;
        this.glossaryList = glossary;
    }

    @Override
    public int getCount() {
        return glossaryList.size();
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
            convertView = inflater.inflate(R.layout.list_row_glossary, null);

        //TextView
        TextView title = (TextView) convertView.findViewById(R.id.title);

        //Glossary Model
        Glossary glossary = glossaryList.get(position);

        title.setText(glossary.getTitle());


        return convertView;
    }

}
