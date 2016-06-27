package org.odk.collect.android.activities;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.database.AfyaDataDB;
import org.odk.collect.android.models.Campaign;
import org.odk.collect.android.provider.FormsProviderAPI;
import org.odk.collect.android.utilities.ImageLoader;

import java.text.ParseException;

public class CampaignActivity extends Activity {
    private Bundle bundle;
    private Campaign campaign = null;
    private AfyaDataDB db;

    TextView title, form_id;
    WebView description;
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
                            //Intent blankForms = new Intent(getApplicationContext(),
                                    //FormChooserList.class);
                            //startActivity(blankForms);
                            // get uri to form
                            long idFormsTable = campaign.getId();
                            Uri formUri = ContentUris.withAppendedId(FormsProviderAPI.FormsColumns.CONTENT_URI, idFormsTable);

                            Collect.getInstance().getActivityLogger().logAction(this, "onListItemClick", formUri.toString());

                            String action = getIntent().getAction();
                            if (Intent.ACTION_PICK.equals(action)) {
                                // caller is waiting on a picked form
                                setResult(RESULT_OK, new Intent().setData(formUri));
                            } else {
                                // caller wants to view/edit a form, so launch formentryactivity
                                startActivity(new Intent(Intent.ACTION_EDIT, formUri));
                            }

                        }
                    });
                }
            }

        }
    }

    public void initializeView() {
        title = (TextView) findViewById(R.id.title);
        form_id = (TextView) findViewById(R.id.form_id);
        description = (WebView) findViewById(R.id.description);
        icon = (ImageView) findViewById(R.id.icon);
        btnForm = (Button) findViewById(R.id.btn_fill_form);
    }

    private void refreshDisplay() throws ParseException {
        title.setText(campaign.getTitle());
        description.loadData(campaign.getDescription(), "text/html", null);

        String uri = "drawable/" + campaign.getIcon();
        int loader = getResources().getIdentifier(uri, "drawable", getPackageName());
        //set icon
        icon.setImageResource(loader);

    }
}
