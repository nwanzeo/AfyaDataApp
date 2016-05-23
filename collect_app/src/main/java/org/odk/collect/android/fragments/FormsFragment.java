package org.odk.collect.android.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.R;
import org.odk.collect.android.activities.SurveyFeedbackActivity;
import org.odk.collect.android.adapters.SurveyFormListAdapter;
import org.odk.collect.android.models.SurveyForm;
import org.odk.collect.android.preferences.PreferencesActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import web.RestClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class FormsFragment extends Fragment {

    private static String TAG = "Forms Fragment";

    private List<SurveyForm> formsList = new ArrayList<SurveyForm>();
    private ListView listSurveyForms;
    private SurveyFormListAdapter adapter;

    private SharedPreferences mSharedPreferences;
    private String username;
    private String password;
    private String serverUrl;
    private ProgressDialog progressDialog;

    private View rootView;


    public FormsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_forms, container, false);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        username = mSharedPreferences.getString(PreferencesActivity.KEY_USERNAME, getResources().getString(R.string.default_sacids_username));
        password = mSharedPreferences.getString(PreferencesActivity.KEY_PASSWORD, getResources().getString(R.string.default_sacids_password));
        serverUrl = mSharedPreferences.getString(PreferencesActivity.KEY_SERVER_URL, getString(R.string.default_server_url));

        //listing form details
        listSurveyForms = (ListView) rootView.findViewById(R.id.lv_survey_forms_status);

        //show progress dialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Wait while loading forms ....");
        progressDialog.show();

        //get Forms from server
        getFormsFromServer();


        //OnClick List view
        listSurveyForms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String form_id = ((TextView) view.findViewById(R.id.form_id))
                        .getText().toString();

                String instance_id = ((TextView) view.findViewById(R.id.instance_id))
                        .getText().toString();

                //start form details Activity here.
                Intent formDetailsIntent = new Intent(getActivity(), SurveyFeedbackActivity.class);
                formDetailsIntent.putExtra("form_id", form_id);
                formDetailsIntent.putExtra("instance_id", instance_id);
                startActivity(formDetailsIntent);
            }
        });

        return rootView;
    }


    private void getFormsFromServer() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        if (ni == null || !ni.isConnected()) {
            Toast.makeText(getActivity(), R.string.no_connection, Toast.LENGTH_SHORT).show();
        }

        final RequestParams params = new RequestParams();
        params.add("username", username);

        String formsURL = serverUrl + "/feedback/feedback_forms";

        // research/feedback/get_feedback
        RestClient.get(username, password, formsURL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressDialog.dismiss();

                Log.d(TAG, response.toString());
                Log.d(TAG, headers.toString());

                if (statusCode == 200) {
                    try {
                        formsList.removeAll(formsList);
                        formsList = getFormsFromJsonResponse(response.getJSONArray("forms"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d(TAG, "Found " + formsList.size() + " forms");

                    //feedbackList adapter
                    adapter = new SurveyFormListAdapter(getActivity(), formsList);
                    listSurveyForms.setAdapter(adapter);


                } else if (statusCode == 204) {
                    Log.d(TAG, "No Forms at the moment");
                    Toast.makeText(getActivity(), "No Forms at the moment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progressDialog.dismiss();

                if (statusCode == 401) {
                    //TODO apply authentication here
                    Toast.makeText(getActivity(), "Unauthorized " + responseString, Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, headers.toString());
                Log.d(TAG, "Failed " + responseString);
            }
        });
    }

    private List<SurveyForm> getFormsFromJsonResponse(JSONArray jsonArray) throws JSONException {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("dd-MM-yyyy HH:mm:ss");
        gsonBuilder.setFieldNamingStrategy(new FieldNamingStrategy() {

            @Override
            public String translateName(Field field) {
                if (field.getName().equals("displayName"))
                    return "title";

                if (field.getName().equals("jrFormId"))
                    return "form_id";

                if (field.getName().equals("jrInstanceId"))
                    return "instance_id";

                if (field.getName().equals("displaySubText"))
                    return "date_created";

                return field.getName();
            }
        });
        Gson gson = gsonBuilder.create();
        Type listType = new TypeToken<List<SurveyForm>>() {
        }.getType();
        return gson.fromJson(jsonArray.toString(), listType);
    }


}
