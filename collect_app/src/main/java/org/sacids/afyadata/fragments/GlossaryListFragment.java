package org.sacids.afyadata.fragments;


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
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sacids.afyadata.R;
import org.sacids.afyadata.activities.GlossaryActivity;
import org.sacids.afyadata.adapters.GlossaryListAdapter;
import org.sacids.afyadata.database.AfyaDataDB;
import org.sacids.afyadata.models.Glossary;
import org.sacids.afyadata.preferences.PreferencesActivity;
import org.sacids.afyadata.prefs.Preferences;

import java.util.ArrayList;
import java.util.List;

import org.sacids.afyadata.web.BackgroundClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class GlossaryListFragment extends Fragment {

    private static String TAG = "Glossary Fragment";
    private View rootView;

    private List<Glossary> glossaryList = new ArrayList<Glossary>();
    private ListView listView;
    private GlossaryListAdapter glossaryListAdapter;

    private SharedPreferences mSharedPreferences;
    private String serverUrl;
    private String language;

    //AfyaData database
    private AfyaDataDB db;

    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CODE = "code";
    private static final String TAG_DESCRIPTION = "description";

    private ProgressDialog pDialog;


    public GlossaryListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_glossary_list, container, false);

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        serverUrl = mSharedPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        //TODO language request
        language = mSharedPreferences.getString(Preferences.DEFAULT_LOCALE, null);

        listView = (ListView) rootView.findViewById(R.id.list_glossary);

        db = new AfyaDataDB(getActivity());

        glossaryList = db.getAllGlossary();

        if (glossaryList.size() > 0) {
            refreshDisplay();
        }

        //check network connectivity
        if (ni == null || !ni.isConnected())
            Toast.makeText(getActivity(), R.string.no_connection, Toast.LENGTH_SHORT).show();
        else
            new FetchGlossaryTask().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Glossary glossary = glossaryList.get(position);
                Intent intent = new Intent(getActivity(), GlossaryActivity.class);
                intent.putExtra(".models.Glossary", glossary);
                startActivity(intent);
            }
        });


        return rootView;
    }

    //refresh display
    private void refreshDisplay() {
        glossaryListAdapter = new GlossaryListAdapter(getActivity(), glossaryList);
        listView.setAdapter(glossaryListAdapter);
        glossaryListAdapter.notifyDataSetChanged();
    }


    class FetchGlossaryTask extends AsyncTask<Void, Void, Void> {

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

            String tipsURL = serverUrl + "/api/v1/ohkr/get_symptoms";

            BackgroundClient.get(tipsURL, param, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    try {
                        if (response.getString("status").equalsIgnoreCase("success")) {
                            JSONArray symptomArray = response.getJSONArray("symptom");

                            for (int i = 0; i < symptomArray.length(); i++) {
                                JSONObject obj = symptomArray.getJSONObject(i);
                                Glossary gs = new Glossary();
                                gs.setId(obj.getInt(TAG_ID));
                                gs.setTitle(obj.getString(TAG_TITLE));
                                gs.setCode(obj.getString(TAG_CODE));
                                gs.setDescription(obj.getString(TAG_DESCRIPTION));

                                if (!db.isGlossaryExist(gs)) {
                                    db.addGlossary(gs);
                                } else {
                                    db.updateGlossary(gs);
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
            glossaryList = db.getAllGlossary();

            if (glossaryList.size() > 0) {
                refreshDisplay();
            }
            pDialog.dismiss();
        }
    }

}
