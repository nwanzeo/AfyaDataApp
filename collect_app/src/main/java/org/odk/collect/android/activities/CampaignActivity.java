package org.odk.collect.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.android.database.AfyaDataDB;
import org.odk.collect.android.models.Campaign;
import org.odk.collect.android.utilities.ImageLoader;

import java.text.ParseException;

public class CampaignActivity extends Activity {
    private Bundle bundle;
    private Campaign campaign = null;
    private AfyaDataDB db;

    TextView title, form_id, description;
    private ImageView icon;
    private Button btnForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign);

        bundle = getIntent().getExtras();
        campaign = new Campaign();
        db = new AfyaDataDB(this);

        //initialize form
        initializeView();

        if (bundle != null) {
            campaign = bundle.getParcelable(".models.Campaign");

            if (campaign != null) {
                campaign.setId(campaign.getId());

                //set Title
                setTitle(getString(R.string.app_name) + " > " + campaign.getTitle());

                try {
                    refreshDisplay();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //handle button visibility
                if (campaign.getType().equalsIgnoreCase("form")) {

                    btnForm.setVisibility(View.VISIBLE);
                    btnForm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //fill blank form
                            Intent blankForms = new Intent(getApplicationContext(),
                                    FormChooserList.class);
                            startActivity(blankForms);
                        }
                    });
                }
            }

        }
    }

    public void initializeView() {
        title = (TextView) findViewById(R.id.title);
        form_id = (TextView) findViewById(R.id.form_id);
        description = (TextView) findViewById(R.id.description);
        icon = (ImageView) findViewById(R.id.icon);
        btnForm = (Button) findViewById(R.id.btn_fill_form);
    }

    private void refreshDisplay() throws ParseException {
        title.setText(campaign.getTitle());
        description.setText(campaign.getDescription());

        String uri = "drawable/" + campaign.getIcon();
        int loader = getResources().getIdentifier(uri, "drawable", getPackageName());
        //set icon
        icon.setImageResource(loader);

    }
}
