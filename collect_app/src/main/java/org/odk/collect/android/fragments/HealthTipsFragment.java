package org.odk.collect.android.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.R;
import org.odk.collect.android.activities.HeathTipsActivity;
import org.odk.collect.android.adapters.DiseaseListAdapter;
import org.odk.collect.android.database.AfyaDataDB;
import org.odk.collect.android.models.Disease;
import org.odk.collect.android.preferences.PreferencesActivity;
import org.odk.collect.android.prefs.Preferences;

import java.util.ArrayList;
import java.util.List;

import web.BackgroundClient;


public class HealthTipsFragment extends Fragment {

    private static String TAG = "Health Tips Fragment";
    private View rootView;

    private List<Disease> diseaseList = new ArrayList<Disease>();
    private ListView listView;
    private DiseaseListAdapter diseaseAdapter;

    private SharedPreferences mSharedPreferences;
    private String serverUrl;
    private String language;

    //AfyaData database
    private AfyaDataDB db;

    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "disease_title";
    private static final String TAG_SPECIE_TITLE = "specie_title";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_CAUSES = "cause";
    private static final String TAG_SYMPTOMS = "symptoms";
    private static final String TAG_DIAGNOSIS = "diagnosis";
    private static final String TAG_TREATMENT = "treatment";

    private ProgressDialog pDialog;


    public HealthTipsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_health_tips, container, false);

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        serverUrl = mSharedPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        //TODO language request
        language = mSharedPreferences.getString(Preferences.DEFAULT_LOCALE, null);

        listView = (ListView) rootView.findViewById(R.id.list_tips);

        db = new AfyaDataDB(getActivity());

        diseaseList = db.getAllDisease();

        if (diseaseList.size() > 0) {
            refreshDisplay();
        }

        //check network connectivity
        if (ni == null || !ni.isConnected())
            Toast.makeText(getActivity(), R.string.no_connection, Toast.LENGTH_SHORT).show();
        else
            new FetchTipsTask().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Disease disease = diseaseList.get(position);
                Intent intent = new Intent(getActivity(), HeathTipsActivity.class);
                intent.putExtra(".models.Disease", disease);
                startActivity(intent);
            }
        });

        return rootView;
    }

    //refresh display
    private void refreshDisplay() {
        diseaseAdapter = new DiseaseListAdapter(getActivity(), diseaseList);
        listView.setAdapter(diseaseAdapter);
        diseaseAdapter.notifyDataSetChanged();
    }


    class FetchTipsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // Progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setCancelable(true);
            pDialog.setMessage(getResources().getString(R.string.lbl_login_message));
            pDialog.show();

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            RequestParams param = new RequestParams();
            param.add("language", language);

            String tipsURL = serverUrl + "/api/v1/ohkr/get_diseases";

            BackgroundClient.get(tipsURL, param, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    try {
                        if (response.getString("status").equalsIgnoreCase("success")) {
                            JSONArray diseaseArray = response.getJSONArray("disease");

                            for (int i = 0; i < diseaseArray.length(); i++) {
                                JSONObject obj = diseaseArray.getJSONObject(i);
                                Disease ds = new Disease();
                                ds.setId(obj.getInt(TAG_ID));
                                ds.setTitle(obj.getString(TAG_TITLE));
                                ds.setSpecie_title(obj.getString(TAG_SPECIE_TITLE));
                                ds.setDescription(obj.getString(TAG_DESCRIPTION));
                                ds.setCauses(obj.getString(TAG_CAUSES));
                                ds.setSymptoms(obj.getString(TAG_SYMPTOMS));
                                ds.setDiagnosis(obj.getString(TAG_DIAGNOSIS));
                                ds.setTreatment(obj.getString(TAG_TREATMENT));

                                if (!db.isDiseaseExist(ds)) {
                                    db.addDisease(ds);
                                } else {
                                    db.updateDisease(ds);
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.d(TAG, "on Failure " + responseString);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            diseaseList = db.getAllDisease();

            if (diseaseList.size() > 0) {
                refreshDisplay();
            }
            pDialog.dismiss();
        }
    }
}


