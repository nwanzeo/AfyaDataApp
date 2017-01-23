package org.sacids.afyadata.fragments;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.sacids.afyadata.R;
import org.sacids.afyadata.activities.SearchResultActivity;
import org.sacids.afyadata.database.AfyaDataDB;
import org.sacids.afyadata.models.SearchableData;
import org.sacids.afyadata.models.SearchableForm;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private static String TAG = "Search Fragment";
    private View rootView;

    private Spinner inputForm;
    private Spinner inputLabel;
    private EditText inputValue;

    private long formId;
    private String jrFormId;
    private String label;
    private String value;

    private AfyaDataDB db;

    private ProgressDialog pd;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        db = new AfyaDataDB(getActivity());

        setUpView();

        // Form Spinner
        loadSpinnerForm();

        //data value spinner
        loadSpinnerData();

        rootView.findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value = inputValue.getText().toString();

                if (value == null || value.length() == 0) {
                    inputValue.setError(getString(R.string.required_search_value));
                } else {
                    Intent searchIntent = new Intent(getActivity(), SearchResultActivity.class);
                    searchIntent.putExtra("form_id", formId);
                    searchIntent.putExtra("jr_form_id", jrFormId);
                    searchIntent.putExtra("search_for", value);
                    searchIntent.putExtra("field", label);
                    startActivity(searchIntent);
                }
            }
        });

        return rootView;
    }

    //set Up View
    public void setUpView() {
        inputForm = (Spinner) rootView.findViewById(R.id.form);
        inputLabel = (Spinner) rootView.findViewById(R.id.label);
        inputValue = (EditText) rootView.findViewById(R.id.value);
    }

    //spinner for form
    private void loadSpinnerForm() {
        // Spinner Drop down elements
        final List<SearchableForm> formList = db.getSearchableForms();

        ArrayList<String> StringFormList = new ArrayList<>();
        //StringFormList.add("Choose Form");
        for (SearchableForm form : formList
                ) {
            StringFormList.add(form.getTitle());
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, StringFormList);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        inputForm.setAdapter(dataAdapter);

        inputForm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SearchableForm sf = formList.get(position);
                formId = sf.getId(); //formId
                jrFormId = sf.getJrFormId(); //jrFormId
                Log.d(TAG, "selected " + sf.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //spinner for data value
    private void loadSpinnerData() {
        // Spinner Drop down elements
        final List<SearchableData> dataList = db.getSearchableData();

        ArrayList<String> StringDataList = new ArrayList<>();
        for (SearchableData data : dataList
                ) {
            StringDataList.add(data.getLabel());
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, StringDataList);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        inputLabel.setAdapter(dataAdapter);

        inputLabel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SearchableData sf = dataList.get(position);
                label = sf.getValue();
                Log.d(TAG, "selected " + sf.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}