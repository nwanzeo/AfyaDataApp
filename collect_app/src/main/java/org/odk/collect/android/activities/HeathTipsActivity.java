package org.odk.collect.android.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.R;
import org.odk.collect.android.adapters.DiseaseListAdapter;
import org.odk.collect.android.database.AfyaDataDB;
import org.odk.collect.android.models.Disease;
import org.odk.collect.android.preferences.PreferencesActivity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import web.RestClient;

public class HeathTipsActivity extends Activity {

    private Bundle bundle;
    private Disease disease = null;
    private AfyaDataDB db;

    TextView title, description, causes, symptoms, diagnosis, treatment;

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
        description = (TextView) findViewById(R.id.description);
        causes = (TextView) findViewById(R.id.causes);
        symptoms = (TextView) findViewById(R.id.symptoms);
        diagnosis = (TextView) findViewById(R.id.diagnosis);
        treatment = (TextView) findViewById(R.id.treatment);
    }

    private void refreshDisplay() throws ParseException {
        title.setText(disease.getTitle());
        description.setText(Html.fromHtml(disease.getDescription()));
        causes.setText(Html.fromHtml(disease.getCauses()));
        symptoms.setText(Html.fromHtml(disease.getSymptoms()));
        diagnosis.setText(Html.fromHtml(disease.getDiagnosis()));
        treatment.setText(Html.fromHtml(disease.getTreatment()));
    }

}
