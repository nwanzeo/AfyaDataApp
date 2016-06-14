package org.odk.collect.android.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
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

    private static String TAG = "Health Tips Fragment";

    private List<Disease> diseaseList = new ArrayList<Disease>();
    private ListView listView;
    private DiseaseListAdapter adapter;
    private String serverUrl;

    private ProgressDialog progressDialog;
    private SharedPreferences mSharedPreferences;

    //AfyaData database
    private AfyaDataDB db;

    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "disease_title";
    private static final String TAG_SPECIE_TITLE = "specie_title";
    private static final String TAG_DESCRIPTION = "description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heath_tips);

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        serverUrl = mSharedPreferences.getString(PreferencesActivity.KEY_SERVER_URL, getString(R.string.default_server_url));

        listView = (ListView) findViewById(R.id.list_tips);

        //show progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Wait while loading tips ....");
        progressDialog.show();

        db = new AfyaDataDB(this);

        diseaseList = db.getAllDisease();

        if (diseaseList.size() > 0) {
            refreshDisplay();
        }

        //check network connectivity
        if (ni == null || !ni.isConnected())
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
        else
            loadDisease();


    }

    private List<Disease> getDiseaseList(JSONArray disease) throws JSONException, ParseException {
        for (int i = 0; i < disease.length(); i++) {
            JSONObject obj = disease.getJSONObject(i);

            //Disease Object
            Disease ds = new Disease();
            ds.setId(obj.getInt(TAG_ID));
            ds.setTitle(obj.getString(TAG_TITLE));
            ds.setSpecie_title(obj.getString(TAG_SPECIE_TITLE));

            //add campaign to a list
            diseaseList.add(ds);

            if (!db.isDiseaseExist(ds)) {
                db.addDisease(ds);

            } else {
                db.updateDisease(ds);
            }
        }
        return diseaseList;
    }

    //refresh display
    private void refreshDisplay() {
        adapter = new DiseaseListAdapter(this, diseaseList);
        listView.setAdapter(adapter);
        progressDialog.dismiss();
    }


    //get data from server
    public void loadDisease() {

        RequestParams params = new RequestParams();

        String diseaseURL = serverUrl + "/ohkr/get_diseases";

        RestClient.get(diseaseURL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressDialog.dismiss();
                Log.d(TAG, "Server Response:" + response.toString());

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {
                        JSONArray diseaseArray = response.getJSONArray("disease");
                        diseaseList = getDiseaseList(diseaseArray);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d(TAG, "on Failure " + responseString);
                Toast.makeText(HeathTipsActivity.this, "Unauthorized", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                super.onCancel();
                progressDialog.dismiss();
            }
        });
    }

}
