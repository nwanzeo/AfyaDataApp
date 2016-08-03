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
    WebView description, symptoms, causes, diagnosis, treatment;

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

    public void initializeView() {
        title = (TextView) findViewById(R.id.title);
        description = (WebView) findViewById(R.id.description);
        causes = (WebView) findViewById(R.id.causes);
        symptoms = (WebView) findViewById(R.id.symptoms);
        diagnosis = (WebView) findViewById(R.id.diagnosis);
        treatment = (WebView) findViewById(R.id.treatment);
    }

    private void refreshDisplay() throws ParseException {
        title.setText(disease.getTitle());

        //check description
        if (disease.getDescription() != "") {
            description.setVisibility(View.VISIBLE);
            description.loadData(disease.getDescription(), "text/html", null);
        }

        //check causes
        if (disease.getCauses() != "") {
            causes.setVisibility(View.VISIBLE);
            causes.loadData(disease.getCauses(), "text/html", null);
        }

        //check symptoms
        if (disease.getSymptoms() != "") {
            symptoms.setVisibility(View.VISIBLE);
            symptoms.loadData(disease.getSymptoms(), "text/html", null);
        }

        //check diagnosis
        if (disease.getDiagnosis() != "") {
            diagnosis.setVisibility(View.VISIBLE);
            diagnosis.loadData(disease.getDiagnosis(), "text/html", null);
        }

        //check if treatment
        if (disease.getTreatment() != "") {
            treatment.setVisibility(View.VISIBLE);
            treatment.loadData(disease.getTreatment(), "text/html", null);
        }
    }

}
