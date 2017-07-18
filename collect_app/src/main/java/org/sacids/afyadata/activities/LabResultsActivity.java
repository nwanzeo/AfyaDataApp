/*
 * Copyright (C) 2016 Sacids Tanzania
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.sacids.afyadata.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sacids.afyadata.R;
import org.sacids.afyadata.adapters.LabRequestAdapter;
import org.sacids.afyadata.application.Collect;
import org.sacids.afyadata.models.LabRequest;
import org.sacids.afyadata.preferences.PreferencesActivity;
import org.sacids.afyadata.prefs.Preferences;
import org.sacids.afyadata.provider.FormsProviderAPI;
import org.sacids.afyadata.web.RestClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for lab results issues
 * directory.
 *
 * @author Renfrid Ngolongolo (renfrid.ngolongolo@sacids.org)
 * @author Godluck Akyoo (godluck.akyoo@sacids.org)
 */

public class LabResultsActivity extends Activity {

    private static String TAG = "Lab Results";
    private Context context = this;

    private List<LabRequest> labRequestList = new ArrayList<LabRequest>();
    private ListView listView;
    private LabRequestAdapter adapter;

    private SharedPreferences mSharedPreferences;
    private String serverUrl;
    private String language;

    private Button btnFillResult;
    private ImageButton btnSearch;
    private EditText searchText;
    private String admissionNumber;


    private static final String TAG_ID = "id";
    private static final String TAG_LABEL = "label";
    private static final String TAG_VALUE = "value";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_results);

        //Set Activity Title
        setTitle(getString(R.string.app_name) + " > Lab Results");

        listView = (ListView) findViewById(R.id.list);
        adapter = new LabRequestAdapter(this, labRequestList);
        listView.setAdapter(adapter);

        btnFillResult = (Button) findViewById(R.id.fill_results);
        btnSearch = (ImageButton) findViewById(R.id.button_search);
        searchText = (EditText) findViewById(R.id.search_text);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                admissionNumber = searchText.getText().toString();

                if (searchText.getText().length() < 1) {
                    searchText.setError(getResources().getString(R.string.required_admission));
                } else {
                    fetchLabDetails();
                }
            }
        });

        //
        btnFillResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String jrFormId = "build_Lab-Results_27";
                String[] selectionArgs = {jrFormId};
                String selection = FormsProviderAPI.FormsColumns.JR_FORM_ID + "=?";
                String[] fields = {FormsProviderAPI.FormsColumns._ID};

                Cursor formCursor = null;
                try {
                    formCursor = Collect.getInstance().getContentResolver().query(FormsProviderAPI.FormsColumns.CONTENT_URI, fields, selection, selectionArgs, null);
                    if (formCursor.getCount() == 0) {
                        // form does not already exist locally
                        //Download form from server
                        Intent downloadForms = new Intent(context,
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

    //get lab details from server
    private void fetchLabDetails() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        if (ni == null || !ni.isConnected()) {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
        }

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        serverUrl = mSharedPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        //TODO language request
        language = mSharedPreferences.getString(Preferences.DEFAULT_LOCALE, null);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getResources().getString(R.string.lbl_login_message));
        progressDialog.show();

        RequestParams params = new RequestParams();
        params.add("v", admissionNumber);

        String labRequestURL = serverUrl + "/api/v3/feedback/requests";

        RestClient.get(labRequestURL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {
                        JSONArray labArray = response.getJSONArray("details");

                        Log.d(TAG, "Message: " + labArray);

                        for (int i = 0; i < labArray.length(); i++) {
                            JSONObject obj = labArray.getJSONObject(i);
                            LabRequest lab = new LabRequest();
                            lab.setId(obj.getInt(TAG_ID));
                            lab.setLabel(obj.getString(TAG_LABEL));
                            lab.setValue(obj.getString(TAG_VALUE));
                            //add to list
                            labRequestList.add(lab);
                        }
                        adapter.notifyDataSetChanged();
                        btnFillResult.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(context, getString(R.string.no_content), Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d(TAG, "Failed " + responseString);
                progressDialog.dismiss();
            }
        });
    }

}
