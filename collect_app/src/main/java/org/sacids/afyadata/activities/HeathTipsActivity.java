package org.sacids.afyadata.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import org.parceler.Parcels;
import org.sacids.afyadata.R;
import org.sacids.afyadata.models.Disease;

public class HeathTipsActivity extends Activity {

    private Disease disease = null;

    TextView title;
    TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heath_tips);

        disease = (Disease) Parcels.unwrap(getIntent().getParcelableExtra("disease"));

        //set Title
        setTitle(getString(R.string.app_name) + " > " + disease.getTitle());

        initializeView();
    }

    //initialize view
    public void initializeView() {
        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);

        title.setText(disease.getTitle());
        description.setText(Html.fromHtml(disease.getDescription()));
    }

}
