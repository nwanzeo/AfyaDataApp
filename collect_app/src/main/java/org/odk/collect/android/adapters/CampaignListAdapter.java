package org.odk.collect.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

import org.odk.collect.android.R;
import org.odk.collect.android.models.Campaign;
import org.odk.collect.android.utilities.ImageLoader;

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
        ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView id = (TextView) convertView.findViewById(R.id.id);

        //Campaign Model
        Campaign cmp = campaignList.get(position);

        String uri = "drawable/" + cmp.getIcon();
        int loader = activity.getResources().getIdentifier(uri, "drawable", activity.getPackageName());

        title.setText(cmp.getTitle());
        id.setText(String.valueOf(cmp.getId()));
        //display image
        icon.setImageResource(loader);

        return convertView;
    }

}
