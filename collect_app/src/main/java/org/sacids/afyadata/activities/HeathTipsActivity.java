package org.sacids.afyadata.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import org.sacids.afyadata.R;
import org.sacids.afyadata.database.AfyaDataDB;
import org.sacids.afyadata.models.Disease;

import java.text.ParseException;

public class HeathTipsActivity extends Activity {

    private Bundle bundle;
    private Disease disease = null;
    private AfyaDataDB db;

    TextView title;
    WebView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heath_tips);

        bundle = getIntent().getExtras();
        disease = new Disease();
        db = new AfyaDataDB(this);

        //initialize form
        initializeView();

        if (bundle != null) {
            disease = bundle.getParcelable(".models.Disease");

            if (disease != null) {
                disease.setId(disease.getId());

                //set Title
                setTitle(getString(R.string.app_name) + " > " + disease.getTitle());

                try {
                    refreshDisplay();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Initialize view
     */
    public void initializeView() {
        title = (TextView) findViewById(R.id.title);
        description = (WebView) findViewById(R.id.description);
    }

    /**
     * refresh display
     * @throws ParseException
     */
    private void refreshDisplay() throws ParseException {
        title.setText(disease.getTitle());

        //check description
        if (disease.getDescription() != "") {
            description.setVisibility(View.VISIBLE);
            description.loadData(disease.getDescription(), "text/html", null);
        }
    }

}
