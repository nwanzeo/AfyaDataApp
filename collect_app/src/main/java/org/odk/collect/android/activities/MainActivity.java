package org.odk.collect.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.odk.collect.android.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_fill_form:
                //fill blank form
                Intent blankForms = new Intent(getApplicationContext(),
                        FormChooserList.class);
                startActivity(blankForms);
                return true;

            case R.id.action_edit_form:
                //Edit forms
                Intent editForms = new Intent(getApplicationContext(),
                        InstanceChooserList.class);
                startActivity(editForms);
                return true;

            case R.id.action_send_form:
                //send finalized Forms
                Intent sendForms = new Intent(getApplicationContext(),
                        InstanceUploaderList.class);
                startActivity(sendForms);
                return true;

            case R.id.action_delete_form:
                //delete saved forms
                Intent deleteForms = new Intent(getApplicationContext(),
                        FileManagerTabs.class);
                startActivity(deleteForms);
                return true;

            case R.id.action_download_form:
                //Download form from server
                Intent downloadForms = new Intent(getApplicationContext(),
                        FormDownloadList.class);
                startActivity(downloadForms);
                return true;
            
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
