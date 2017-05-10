package org.sacids.afyadata.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.sacids.afyadata.R;
import org.sacids.afyadata.activities.FileManagerTabs;
import org.sacids.afyadata.activities.FormChooserList;
import org.sacids.afyadata.activities.FormDownloadList;
import org.sacids.afyadata.activities.InstanceChooserList;
import org.sacids.afyadata.activities.InstanceUploaderList;
import org.sacids.afyadata.adapters.MenuGridAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {

    // Log tag
    private static final String TAG = "Menu Fragment";
    private GridView gridView;
    private MenuGridAdapter menuAdapter;

    private String[] menuTitle;

    private Integer[] menuImage = {
            R.drawable.ic_fill_blank_form,
            R.drawable.ic_edit_saved_form,
            R.drawable.ic_upload_form,
            R.drawable.ic_trash_bin,
            R.drawable.ic_download_form
    };

    private View rootView;


    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_menu, container, false);

        menuTitle = new String[]{
                getResources().getString(R.string.enter_data_button),
                getResources().getString(R.string.review_data),
                getResources().getString(R.string.send_data),
                getResources().getString(R.string.manage_files),
                getResources().getString(R.string.get_forms)
        };

        //gridView
        gridView = (GridView) rootView.findViewById(R.id.grid_menu);

        menuAdapter = new MenuGridAdapter(getActivity(), menuTitle, menuImage);
        gridView.setAdapter(menuAdapter);

        //Onclick Listener ListView
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                switch (position) {
                    case 0:
                        startActivity(new Intent(getActivity(), FormChooserList.class));
                        return;

                    case 1:
                        startActivity(new Intent(getActivity(), InstanceChooserList.class));
                        return;

                    case 2:
                        startActivity(new Intent(getActivity(), InstanceUploaderList.class));
                        return;

                    case 3:
                        startActivity(new Intent(getActivity(), FileManagerTabs.class));
                        return;

                    case 4:
                        startActivity(new Intent(getActivity(), FormDownloadList.class));
                        return;

                    default:
                        break;
                }
            }
        });

        return rootView;
    }

}
