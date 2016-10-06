package org.sacids.afyadata.fragments;


import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.sacids.afyadata.R;
import org.sacids.afyadata.activities.FormDownloadList;
import org.sacids.afyadata.activities.LabResultsActivity;
import org.sacids.afyadata.application.Collect;
import org.sacids.afyadata.provider.FormsProviderAPI;

/**
 * A simple {@link Fragment} subclass.
 */
public class LaboratoryFragment extends Fragment {

    private static String TAG = "Laboratory Fragment";
    private View rootView;

    private Button btnLabRequest;
    private Button btnLabResults;

    public LaboratoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_laboratory, container, false);

        setUpView();

        //In case of lab request
        btnLabRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String jrFormId = "build_Lab-Request_1475596584";
                String[] selectionArgs = {jrFormId};
                String selection = FormsProviderAPI.FormsColumns.JR_FORM_ID + "=?";
                String[] fields = {FormsProviderAPI.FormsColumns._ID};

                Cursor formCursor = null;
                try {
                    formCursor = Collect.getInstance().getContentResolver().query(FormsProviderAPI.FormsColumns.CONTENT_URI, fields, selection, selectionArgs, null);
                    if (formCursor.getCount() == 0) {
                        // form does not already exist locally
                        //Download form from server
                        Intent downloadForms = new Intent(getActivity(),
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

                        String action = getActivity().getIntent().getAction();
                        if (Intent.ACTION_PICK.equals(action)) {
                            // caller is waiting on a picked form
                            getActivity().setResult(getActivity().RESULT_OK, new Intent().setData(formUri));
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

        //in case of lab results
        btnLabResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent labResult = new Intent(getActivity(), LabResultsActivity.class);
                startActivity(labResult);
            }
        });

        return rootView;
    }

    //set Up View
    public void setUpView() {
        btnLabRequest = (Button) rootView.findViewById(R.id.lab_request);
        btnLabResults = (Button) rootView.findViewById(R.id.lab_result);
    }

}
