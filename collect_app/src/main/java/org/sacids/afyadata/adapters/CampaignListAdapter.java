package org.sacids.afyadata.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.sacids.afyadata.R;
import org.sacids.afyadata.models.Campaign;
import org.sacids.afyadata.utilities.ImageLoader;

import java.util.List;

/**
 * Created by Renfrid-Sacids on 5/25/2016.
 */
public class CampaignListAdapter extends BaseAdapter {

    private Activity activity;
    private List<Campaign> campaignList;
    private LayoutInflater inflater;

    public CampaignListAdapter(Activity activity, List<Campaign> myCampaign) {
        this.activity = activity;
        this.campaignList = myCampaign;
    }

    @Override
    public int getCount() {
        return campaignList.size();
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
            convertView = inflater.inflate(R.layout.grid_row_campaign, null);

        //TextView
        TextView title = (TextView) convertView.findViewById(R.id.title);
        ImageView icon = (ImageView) convertView.findViewById(R.id.icon);

        // Loader image - will be shown before loading image
        int loader = R.drawable.ic_afyadata_one;

        //Campaign Model
        Campaign cmp = campaignList.get(position);

        title.setText(cmp.getTitle());

        // ImageLoader class instance
        ImageLoader imgLoader = new ImageLoader(activity);

        //load image
        imgLoader.displayImage(cmp.getIcon(), loader, icon);

        return convertView;
    }

}
