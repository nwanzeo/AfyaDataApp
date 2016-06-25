package org.odk.collect.android.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.android.database.AfyaDataDB;
import org.odk.collect.android.models.Glossary;

import java.text.ParseException;

public class GlossaryActivity extends Activity {

    private Bundle bundle;
    private Glossary glossary= null;
    private AfyaDataDB db;

    TextView title, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glossary);

        bundle = getIntent().getExtras();
        glossary = new Glossary();
        db = new AfyaDataDB(this);

        //initialize form
        initializeView();

        if (bundle != null) {
            glossary = bundle.getParcelable(".models.Glossary");

            if (glossary != null) {
                glossary.setId(glossary.getId());

                //set Title
                setTitle(getString(R.string.app_name) + " > Glossary");
                try {
                    refreshDisplay();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void initializeView() {
        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
    }

    private void refreshDisplay() throws ParseException {
        title.setText(glossary.getTitle());
        description.setText(Html.fromHtml(glossary.getDescription()));
    }
}
