package org.sacids.afyadata.activities;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.sacids.afyadata.R;
import org.sacids.afyadata.application.Collect;
import org.sacids.afyadata.database.AfyaDataDB;
import org.sacids.afyadata.models.Campaign;
import org.sacids.afyadata.provider.FormsProviderAPI;

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
                            String formId = campaign.getFormId();
                            String[] selectionArgs = {formId};
                            String selection = FormsProviderAPI.FormsColumns.JR_FORM_ID + "=?";
                            String[] fields = {FormsProviderAPI.FormsColumns._ID};

                            Cursor formCursor = null;
                            try {
                                formCursor = Collect.getInstance().getContentResolver().query(FormsProviderAPI.FormsColumns.CONTENT_URI, fields, selection, selectionArgs, null);
                                if (formCursor.getCount() == 0) {
                                    // form does not already exist locally
                                    //Download form from server
                                    Intent downloadForms = new Intent(getApplicationContext(),
                                            FormDownloadList.class);
                                    startActivity(downloadForms);
                                } else {
                                    formCursor.moveToFirst();
                                    long idFormsTable = Long.parseLong(formCursor.getString(
                                            formCursor.getColumnIndex(FormsProviderAPI.FormsColumns._ID)));
                                    Uri formUri = ContentUris.withAppendedId(
                                            FormsProviderAPI.FormsColumns.CONTENT_URI, idFormsTable);

                                    Collect.getInstance().getActivityLogger()
                                            .logAction(this, "onListItemClick: ", formUri.toString());

                                    String action = getIntent().getAction();
                                    if (Intent.ACTION_PICK.equals(action)) {
                                        // caller is waiting on a picked form
                                        setResult(RESULT_OK, new Intent().setData(formUri));
                                    } else {
                                        // caller wants to view/edit a form, so launch formentryactivity
                                        startActivity(new Intent(Intent.ACTION_EDIT, formUri));
                                    }//end of function
                                }


                            } finally {
                                if (formCursor != null) {
                                    formCursor.close();
                                }
                            }
                        }
                    });
                }
            }

        }
    }

    /**
     * Initialize view
     */
    public void initializeView() {
        title = (TextView) findViewById(R.id.title);
        form_id = (TextView) findViewById(R.id.form_id);
        description = (WebView) findViewById(R.id.description);
        icon = (ImageView) findViewById(R.id.icon);
        btnForm = (Button) findViewById(R.id.btn_fill_form);
    }

    /**
     * Refresh display
     * @throws ParseException
     */
    private void refreshDisplay() throws ParseException {
        title.setText(campaign.getTitle());
        description.loadData(campaign.getDescription(), "text/html", null);

        String uri = "drawable/" + campaign.getIcon();
        int loader = getResources().getIdentifier(uri, "drawable", getPackageName());
        //set icon
        icon.setImageResource(loader);
    }
}
