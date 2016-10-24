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
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import org.parceler.Parcels;
import org.sacids.afyadata.R;
import org.sacids.afyadata.models.Disease;

/**
 * Responsible for health tips
 * directory.
 *
 * @author Renfrid Ngolongolo (renfrid.ngolongolo@sacids.org)
 * @author Godluck Akyoo (godluck.akyoo@sacids.org)
 */

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
