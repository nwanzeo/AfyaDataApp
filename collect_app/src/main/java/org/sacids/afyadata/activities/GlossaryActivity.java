package org.sacids.afyadata.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import org.parceler.Parcels;
import org.sacids.afyadata.R;
import org.sacids.afyadata.models.Glossary;

public class GlossaryActivity extends Activity {

    private Glossary glossary = null;

    TextView title;
    TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glossary);

        glossary = (Glossary) Parcels.unwrap(getIntent().getParcelableExtra("symptom"));

        //set title
        setTitle(getString(R.string.app_name) + " > Symptom");

        initializeView();
    }

    //initialize view
    public void initializeView() {
        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);

        title.setText(glossary.getTitle());
        description.setText(Html.fromHtml(glossary.getDescription()));
    }
}
