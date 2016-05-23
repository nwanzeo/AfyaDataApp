package org.odk.collect.android.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.odk.collect.android.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HealthTipsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HealthTipsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HealthTipsFragment extends Fragment {


    public HealthTipsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_health_tips, container, false);
    }

}
