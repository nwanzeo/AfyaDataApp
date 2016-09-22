package org.sacids.afyadata.activities;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.parceler.Parcels;
import org.sacids.afyadata.R;
import org.sacids.afyadata.application.Collect;
import org.sacids.afyadata.database.AfyaDataDB;
import org.sacids.afyadata.models.Campaign;
import org.sacids.afyadata.provider.FormsProviderAPI;
import org.sacids.afyadata.utilities.ImageLoader;

import java.text.ParseException;

public class CampaignActivity extends Activity {
    private Campaign campaign;

    TextView title;
    TextView description;
    private ImageView icon;

    private Button btnForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign);

        campaign = (Campaign) Parcels.unwrap(getIntent().getParcelableExtra("campaign"));

        //Set Activity Title
        setTitle(getString(R.string.app_name) + " > " + campaign.getTitle());

        setViews();

        //handle button visibility
        if (campaign.getType().equalsIgnoreCase("form")) {

            btnForm.setVisibility(View.VISIBLE);
            btnForm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //fill blank form
                    String jrFormId = campaign.getJrFormId();
                    String[] selectionArgs = {jrFormId};
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
                                // caller wants to view/edit a form, so launch form entry activity
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

    //SetUpViews
    public void setViews() {
        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        icon = (ImageView) findViewById(R.id.icon);

        //setText
        title.setText(campaign.getTitle());
        description.setText(Html.fromHtml(campaign.getDescription()));

        // Loader image - will be shown before loading image
        int loader = R.drawable.ic_afyadata_one;

        ImageLoader imgLoader = new ImageLoader(this);
        imgLoader.displayImage(campaign.getIcon(), loader, icon);

        btnForm = (Button) findViewById(R.id.btn_fill_form);
    }
}
