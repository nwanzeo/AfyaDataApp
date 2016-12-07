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

package org.sacids.afyadata.database;

/**
 * Created by Renfrid-Sacids on 3/16/2016.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.sacids.afyadata.models.Campaign;
import org.sacids.afyadata.models.Disease;
import org.sacids.afyadata.models.Feedback;
import org.sacids.afyadata.models.FormDetails;
import org.sacids.afyadata.models.Glossary;
import org.sacids.afyadata.models.SearchableData;
import org.sacids.afyadata.models.SearchableForm;

public class AfyaDataDB extends SQLiteOpenHelper {

    private static final String TAG = AfyaDataDB.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 12;

    // Database Name
    private static final String DATABASE_NAME = "afyadata.db";

    //searchable form
    public static final String TABLE_SEARCHABLE_FORM = "searchable_form";
    public static final String KEY_SEARCHABLE_FORM_ID = "id";
    public static final String KEY_SEARCHABLE_JR_FORM_ID = "jr_form_id";
    public static final String KEY_SEARCHABLE_FORM_TITLE = "title";

    //searchable form data
    public static final String TABLE_SEARCHABLE_DATA = "searchable_data";
    public static final String KEY_SEARCHABLE_DATA_ID = "id";
    public static final String KEY_SEARCHABLE_DATA_FORM_ID = "form_id";
    public static final String KEY_SEARCHABLE_DATA_LABEL = "label";
    public static final String KEY_SEARCHABLE_DATA_VALUE = "value";

    // Feedback table
    public static final String TABLE_FEEDBACK = "feedback";
    public static final String KEY_FEEDBACK_ID = "id";
    public static final String KEY_FEEDBACK_FORM_ID = "form_id";
    public static final String KEY_INSTANCE_ID = "instance_id";
    public static final String KEY_FORM_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_SENDER = "sender";
    public static final String KEY_USER = "user";
    public static final String KEY_CHR_NAME = "chr_name";
    public static final String KEY_DATE_CREATED = "date_created";
    public static final String KEY_FEEDBACK_STATUS = "status";
    public static final String KEY_FEEDBACK_REPLY_BY = "reply_by";

    private Feedback feedback = null;

    //Campaign table
    public static final String TABLE_CAMPAIGN = "campaign";
    public static final String KEY_CAMPAIGN_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_TYPE = "type";
    public static final String KEY_FEATURED = "featured";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_ICON = "icon";
    public static final String KEY_CAMPAIGN_JR_FORM_ID = "jr_form_id";
    public static final String KEY_CAMPAIGN_DATE_CREATED = "date_created";

    //OHKR disease table
    public static final String TABLE_OHKR_DISEASE = "ohkr_disease";
    public static final String KEY_DISEASE_ID = "id";
    public static final String KEY_DISEASE_TITLE = "title";
    public static final String KEY_DISEASE_DESCRIPTION = "description";
    public static final String KEY_DISEASE_CAUSES = "cause";
    public static final String KEY_DISEASE_SYMPTOMS = "symptoms";
    public static final String KEY_DISEASE_DIAGNOSIS = "diagnosis";
    public static final String KEY_DISEASE_TREATMENT = "treatment";
    public static final String KEY_SPECIE_TITLE = "specie_title";

    //OHKR Glossary List
    public static final String TABLE_OHKR_GLOSSARY = "ohkr_glossary";
    public static final String KEY_GLOSSARY_ID = "id";
    public static final String KEY_GLOSSARY_TITLE = "title";
    public static final String KEY_GLOSSARY_DESCRIPTION = "description";
    public static final String KEY_GLOSSARY_CODE = "code";

    //Form details
    public static final String TABLE_FORM_DETAILS = "form_details";
    public static final String KEY_FORM_DETAILS_ID = "id";
    public static final String KEY_FORM_DETAILS_LABEL = "label";
    public static final String KEY_FORM_DETAILS_TYPE = "type";
    public static final String KEY_FORM_DETAILS_VALUE = "value";
    public static final String KEY_FORM_DETAILS_INSTANCE_ID = "instance_id";

    public AfyaDataDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SEARCHABLE_FORM_TABLE = "CREATE TABLE "
                + TABLE_SEARCHABLE_FORM + "("
                + KEY_SEARCHABLE_FORM_ID + " INTEGER PRIMARY KEY,"
                + KEY_SEARCHABLE_JR_FORM_ID + " TEXT,"
                + KEY_SEARCHABLE_FORM_TITLE + " TEXT" + ")";

        String CREATE_SEARCHABLE_DATA_TABLE = "CREATE TABLE "
                + TABLE_SEARCHABLE_DATA + "("
                + KEY_SEARCHABLE_DATA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_SEARCHABLE_DATA_FORM_ID + " TEXT,"
                + KEY_SEARCHABLE_DATA_LABEL + " TEXT,"
                + KEY_SEARCHABLE_DATA_VALUE + " TEXT" + ")";

        String CREATE_FEEDBACK_TABLE = "CREATE TABLE "
                + TABLE_FEEDBACK + "("
                + KEY_FEEDBACK_ID + " INTEGER PRIMARY KEY,"
                + KEY_FEEDBACK_FORM_ID + " TEXT,"
                + KEY_INSTANCE_ID + " TEXT,"
                + KEY_FORM_TITLE + " TEXT,"
                + KEY_MESSAGE + " TEXT,"
                + KEY_SENDER + " TEXT,"
                + KEY_USER + " TEXT,"
                + KEY_CHR_NAME + " TEXT,"
                + KEY_DATE_CREATED + " TEXT,"
                + KEY_FEEDBACK_STATUS + " TEXT,"
                + KEY_FEEDBACK_REPLY_BY + " TEXT" + ")";

        String CREATE_CAMPAIGN_TABLE = "CREATE TABLE "
                + TABLE_CAMPAIGN + "("
                + KEY_CAMPAIGN_ID + " INTEGER PRIMARY KEY," // and auto increment will be handled with
                + KEY_TITLE + " TEXT,"
                + KEY_TYPE + " TEXT,"
                + KEY_FEATURED + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_ICON + " TEXT,"
                + KEY_CAMPAIGN_JR_FORM_ID + " TEXT,"
                + KEY_CAMPAIGN_DATE_CREATED + " TEXT" + ")";

        String CREATE_DISEASE_TABLE = "CREATE TABLE "
                + TABLE_OHKR_DISEASE + "("
                + KEY_DISEASE_ID + " INTEGER PRIMARY KEY," // and auto increment will be handled with
                + KEY_DISEASE_TITLE + " TEXT,"
                + KEY_DISEASE_DESCRIPTION + " TEXT,"
                + KEY_DISEASE_CAUSES + " TEXT,"
                + KEY_DISEASE_SYMPTOMS + " TEXT,"
                + KEY_DISEASE_DIAGNOSIS + " TEXT,"
                + KEY_DISEASE_TREATMENT + " TEXT,"
                + KEY_SPECIE_TITLE + " TEXT" + ")";

        String CREATE_GLOSSARY_TABLE = "CREATE TABLE "
                + TABLE_OHKR_GLOSSARY + "("
                + KEY_GLOSSARY_ID + " INTEGER PRIMARY KEY," // and auto increment will be handled with
                + KEY_GLOSSARY_TITLE + " TEXT,"
                + KEY_GLOSSARY_CODE + " TEXT,"
                + KEY_DISEASE_DESCRIPTION + " TEXT" + ")";

        String CREATE_FORM_DETAILS_TABLE = "CREATE TABLE "
                + TABLE_FORM_DETAILS + "("
                + KEY_FORM_DETAILS_ID + " INTEGER PRIMARY KEY," // and auto increment will be handled with
                + KEY_FORM_DETAILS_LABEL + " TEXT,"
                + KEY_FORM_DETAILS_TYPE + " TEXT,"
                + KEY_FORM_DETAILS_VALUE + " TEXT,"
                + KEY_FORM_DETAILS_INSTANCE_ID + " TEXT" + ")";

        db.execSQL(CREATE_SEARCHABLE_FORM_TABLE);
        db.execSQL(CREATE_SEARCHABLE_DATA_TABLE);
        db.execSQL(CREATE_FEEDBACK_TABLE);
        db.execSQL(CREATE_CAMPAIGN_TABLE);
        db.execSQL(CREATE_DISEASE_TABLE);
        db.execSQL(CREATE_GLOSSARY_TABLE);
        db.execSQL(CREATE_FORM_DETAILS_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCHABLE_FORM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCHABLE_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDBACK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAMPAIGN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OHKR_DISEASE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OHKR_GLOSSARY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORM_DETAILS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations for Searchable form
     */
    //add searchable form
    public void addSearchableForm(SearchableForm form) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SEARCHABLE_FORM_ID, form.getId());
        values.put(KEY_SEARCHABLE_FORM_TITLE, form.getTitle());
        values.put(KEY_SEARCHABLE_JR_FORM_ID, form.getJrFormId());

        // Inserting Row
        db.insert(TABLE_SEARCHABLE_FORM, null, values);
        db.close(); // Closing database connection
    }

    //getAllSearchable Form
    public List<SearchableForm> getSearchableForms() {

        List<SearchableForm> formList = new ArrayList<SearchableForm>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SEARCHABLE_FORM + " ORDER BY " + KEY_SEARCHABLE_FORM_TITLE + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                SearchableForm form = new SearchableForm();
                form.setId(cursor.getLong(cursor.getColumnIndex(KEY_SEARCHABLE_FORM_ID)));
                form.setJrFormId(cursor.getString(cursor.getColumnIndex(KEY_SEARCHABLE_JR_FORM_ID)));
                form.setTitle(cursor.getString(cursor.getColumnIndex(KEY_SEARCHABLE_FORM_TITLE)));

                // Adding form to list
                formList.add(form);
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();

        // return formList
        return formList;
    }

    //check if searchable exists
    public boolean isSearchableExist(SearchableForm form) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SEARCHABLE_FORM, new String[]{KEY_SEARCHABLE_FORM_ID,
                        KEY_SEARCHABLE_FORM_TITLE, KEY_SEARCHABLE_JR_FORM_ID},
                KEY_SEARCHABLE_FORM_ID + "=?", new String[]{String.valueOf(form.getId())}, null, null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return (count > 0) ? true : false;
    }


    // Updating Searchable Form
    public int updateSearchableForm(SearchableForm form) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SEARCHABLE_FORM_ID, form.getId());
        values.put(KEY_SEARCHABLE_FORM_TITLE, form.getTitle());
        values.put(KEY_SEARCHABLE_JR_FORM_ID, form.getJrFormId());

        // updating row
        return db.update(TABLE_SEARCHABLE_FORM, values, KEY_SEARCHABLE_FORM_ID + " = ?",
                new String[]{String.valueOf(form.getId())});
    }


    /**
     * All CRUD(Create, Read, Update, Delete) Operations for Searchable data
     */
    public void addSearchableData(SearchableData data) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SEARCHABLE_DATA_FORM_ID, data.getFormId());
        values.put(KEY_SEARCHABLE_DATA_LABEL, data.getLabel());
        values.put(KEY_SEARCHABLE_DATA_VALUE, data.getValue());

        // Inserting Row
        db.insert(TABLE_SEARCHABLE_DATA, null, values);
        db.close(); // Closing database connection
    }

    //getAllSearchable Form
    public List<SearchableData> getSearchableData() {

        List<SearchableData> dataList = new ArrayList<SearchableData>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SEARCHABLE_DATA
                + " ORDER BY " + KEY_SEARCHABLE_DATA_VALUE + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                SearchableData data = new SearchableData();
                data.setLabel(cursor.getString(cursor.getColumnIndex(KEY_SEARCHABLE_DATA_LABEL)));
                data.setValue(cursor.getString(cursor.getColumnIndex(KEY_SEARCHABLE_DATA_VALUE)));
                data.setFormId(cursor.getString(cursor.getColumnIndex(KEY_SEARCHABLE_FORM_ID)));

                // Adding form data to list
                dataList.add(data);
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();

        // return dataList
        return dataList;
    }

    //check if searchable data exists
    public boolean isSearchableDataExist(SearchableData data) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SEARCHABLE_DATA, new String[]{KEY_SEARCHABLE_DATA_ID,
                        KEY_SEARCHABLE_FORM_ID, KEY_SEARCHABLE_DATA_LABEL, KEY_SEARCHABLE_DATA_VALUE},
                KEY_SEARCHABLE_DATA_FORM_ID + "=? AND " + KEY_SEARCHABLE_DATA_LABEL + "=?",
                new String[]{data.getFormId(), data.getLabel()}, null, null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return (count > 0) ? true : false;
    }


    // Updating Searchable data
    public int updateSearchableData(SearchableData data) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SEARCHABLE_DATA_FORM_ID, data.getFormId());
        values.put(KEY_SEARCHABLE_DATA_LABEL, data.getLabel());
        values.put(KEY_SEARCHABLE_DATA_VALUE, data.getValue());

        // updating row
        return db.update(TABLE_SEARCHABLE_DATA, values, KEY_SEARCHABLE_DATA_FORM_ID + " = ? AND " +
                        KEY_SEARCHABLE_DATA_LABEL + " = ?",
                new String[]{data.getFormId(), data.getValue()});
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations for Feedback
     */

    // Adding new Feedback
    public void addFeedback(Feedback feedback) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FEEDBACK_ID, feedback.getId());
        values.put(KEY_FEEDBACK_FORM_ID, feedback.getFormId());
        values.put(KEY_INSTANCE_ID, feedback.getInstanceId());
        values.put(KEY_FORM_TITLE, feedback.getTitle());
        values.put(KEY_MESSAGE, feedback.getMessage());
        values.put(KEY_SENDER, feedback.getSender());
        values.put(KEY_USER, feedback.getUserName());
        values.put(KEY_CHR_NAME, feedback.getChrName());
        values.put(KEY_DATE_CREATED, feedback.getDateCreated());
        values.put(KEY_FEEDBACK_STATUS, feedback.getStatus());
        values.put(KEY_FEEDBACK_REPLY_BY, feedback.getReplyBy());

        // Inserting Row
        db.insert(TABLE_FEEDBACK, null, values);
        db.close(); // Closing database connection
    }

    // Getting All Feedback
    public List<Feedback> getAllFeedback() {

        List<Feedback> feedbackList = new ArrayList<Feedback>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FEEDBACK
                + " GROUP BY " + KEY_INSTANCE_ID + " ORDER BY " + KEY_FEEDBACK_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Feedback feedback = new Feedback();
                feedback.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_ID))));
                feedback.setFormId(cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_FORM_ID)));
                feedback.setInstanceId(cursor.getString(cursor.getColumnIndex(KEY_INSTANCE_ID)));
                feedback.setTitle(cursor.getString(cursor.getColumnIndex(KEY_FORM_TITLE)));
                feedback.setMessage(cursor.getString(cursor.getColumnIndex(KEY_MESSAGE)));
                feedback.setSender(cursor.getString(cursor.getColumnIndex(KEY_SENDER)));
                feedback.setUserName(cursor.getString(cursor.getColumnIndex(KEY_USER)));
                feedback.setChrName(cursor.getString(cursor.getColumnIndex(KEY_CHR_NAME)));
                feedback.setDateCreated(cursor.getString(cursor.getColumnIndex(KEY_DATE_CREATED)));
                feedback.setStatus(cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_STATUS)));
                feedback.setReplyBy(cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_REPLY_BY)));

                // Adding feedback to list
                feedbackList.add(feedback);
            } while (cursor.moveToNext());
        }

        // return feedback list
        return feedbackList;
    }


    // Getting Feedback by Instance
    public List<Feedback> getFeedbackByInstance(String instanceId) {

        List<Feedback> feedbackList = new ArrayList<Feedback>();
        // Select All Query based on instanceId
        String selectQuery = "SELECT * FROM " + TABLE_FEEDBACK + " WHERE " + KEY_INSTANCE_ID + " = ?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{instanceId});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Feedback feedback = new Feedback();
                feedback.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_ID))));
                feedback.setFormId(cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_FORM_ID)));
                feedback.setInstanceId(cursor.getString(cursor.getColumnIndex(KEY_INSTANCE_ID)));
                feedback.setTitle(cursor.getString(cursor.getColumnIndex(KEY_FORM_TITLE)));
                feedback.setMessage(cursor.getString(cursor.getColumnIndex(KEY_MESSAGE)));
                feedback.setSender(cursor.getString(cursor.getColumnIndex(KEY_SENDER)));
                feedback.setUserName(cursor.getString(cursor.getColumnIndex(KEY_USER)));
                feedback.setChrName(cursor.getString(cursor.getColumnIndex(KEY_CHR_NAME)));
                feedback.setDateCreated(cursor.getString(cursor.getColumnIndex(KEY_DATE_CREATED)));
                feedback.setStatus(cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_STATUS)));
                feedback.setReplyBy(cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_REPLY_BY)));

                // Adding feedback to list
                feedbackList.add(feedback);
            } while (cursor.moveToNext());
        }

        // return feedback list
        return feedbackList;
    }

    //check if feedback exists
    public boolean isFeedbackExist(Feedback feedback) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FEEDBACK, new String[]{KEY_FEEDBACK_ID,
                        KEY_FEEDBACK_FORM_ID, KEY_INSTANCE_ID, KEY_FORM_TITLE, KEY_MESSAGE, KEY_SENDER, KEY_USER,
                        KEY_CHR_NAME, KEY_DATE_CREATED, KEY_FEEDBACK_STATUS, KEY_FEEDBACK_REPLY_BY},
                KEY_FEEDBACK_ID + "=?", new String[]{String.valueOf(feedback.getId())}, null, null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return (count > 0) ? true : false;
    }


    // Updating single feedback
    public int updateFeedback(Feedback feedback) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_FEEDBACK_FORM_ID, feedback.getFormId());
        values.put(KEY_INSTANCE_ID, feedback.getInstanceId());
        values.put(KEY_FORM_TITLE, feedback.getTitle());
        values.put(KEY_MESSAGE, feedback.getMessage());
        values.put(KEY_SENDER, feedback.getSender());
        values.put(KEY_USER, feedback.getUserName());
        values.put(KEY_CHR_NAME, feedback.getChrName());
        values.put(KEY_DATE_CREATED, feedback.getDateCreated());
        values.put(KEY_FEEDBACK_STATUS, feedback.getStatus());
        values.put(KEY_FEEDBACK_REPLY_BY, feedback.getReplyBy());

        // updating row
        return db.update(TABLE_FEEDBACK, values, KEY_FEEDBACK_ID + " = ?",
                new String[]{String.valueOf(feedback.getId())});
    }

    // Deleting feedback
    public void deleteFeedback(String instanceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FEEDBACK, KEY_INSTANCE_ID + " = ?",
                new String[]{instanceId});
        db.close();
    }

    // Getting feedback Count
    public int getFeedbackCount() {
        int count = 0;
        String countQuery = "SELECT  * FROM " + TABLE_FEEDBACK;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        if (cursor != null && !cursor.isClosed()) {
            count = cursor.getCount();
            cursor.close();
        }

        // return count
        return count;
    }


    //get Last Feedback
    public Feedback getLastFeedback() {
        String selectQuery = "SELECT  * FROM " + TABLE_FEEDBACK + " ORDER BY " + KEY_FEEDBACK_ID + " DESC LIMIT 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //check if cursor not null
        if (cursor != null && cursor.moveToFirst()) {
            //feedback constructor
            feedback = new Feedback(
                    Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_ID))),
                    cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_FORM_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_INSTANCE_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_FORM_TITLE)),
                    cursor.getString(cursor.getColumnIndex(KEY_MESSAGE)),
                    cursor.getString(cursor.getColumnIndex(KEY_SENDER)),
                    cursor.getString(cursor.getColumnIndex(KEY_USER)),
                    cursor.getString(cursor.getColumnIndex(KEY_CHR_NAME)),
                    cursor.getString(cursor.getColumnIndex(KEY_DATE_CREATED)),
                    cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_STATUS)),
                    cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_REPLY_BY)));
        }

        // return feedback
        return feedback;
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations for Campaign
     */

    // Adding new Campaign
    public void addCampaign(Campaign campaign) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CAMPAIGN_ID, campaign.getId());
        values.put(KEY_TITLE, campaign.getTitle());
        values.put(KEY_TYPE, campaign.getType());
        values.put(KEY_FEATURED, campaign.getFeatured());
        values.put(KEY_CAMPAIGN_JR_FORM_ID, campaign.getJrFormId());
        values.put(KEY_ICON, campaign.getIcon());
        values.put(KEY_DESCRIPTION, campaign.getDescription());
        values.put(KEY_CAMPAIGN_DATE_CREATED, campaign.getDateCreated());

        // Inserting Row
        db.insert(TABLE_CAMPAIGN, null, values);
        db.close();
    }

    // Getting single Campaign
    public Campaign getFeaturedCampaign(String featured) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CAMPAIGN, new String[]{KEY_CAMPAIGN_ID,
                        KEY_TITLE, KEY_TYPE, KEY_FEATURED, KEY_CAMPAIGN_JR_FORM_ID, KEY_DESCRIPTION, KEY_ICON,
                        KEY_CAMPAIGN_DATE_CREATED}, KEY_FEATURED + "=?",
                new String[]{featured}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Campaign campaign = new Campaign(
                Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_CAMPAIGN_ID))),
                cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                cursor.getString(cursor.getColumnIndex(KEY_TYPE)),
                cursor.getString(cursor.getColumnIndex(KEY_FEATURED)),
                cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndex(KEY_CAMPAIGN_JR_FORM_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_ICON)),
                cursor.getString(cursor.getColumnIndex(KEY_CAMPAIGN_DATE_CREATED)));

        return campaign;
    }

    // Getting All Campaign
    public List<Campaign> getAllCampaign() {

        List<Campaign> campaignList = new ArrayList<Campaign>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CAMPAIGN + " ORDER BY " + KEY_CAMPAIGN_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Campaign campaign = new Campaign();
                campaign.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_CAMPAIGN_ID))));
                campaign.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                campaign.setType(cursor.getString(cursor.getColumnIndex(KEY_TYPE)));
                campaign.setFeatured(cursor.getString(cursor.getColumnIndex(KEY_FEATURED)));
                campaign.setJrFormId(cursor.getString(cursor.getColumnIndex(KEY_CAMPAIGN_JR_FORM_ID)));
                campaign.setIcon(cursor.getString(cursor.getColumnIndex(KEY_ICON)));
                campaign.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                campaign.setDateCreated(cursor.getString(cursor.getColumnIndex(KEY_CAMPAIGN_DATE_CREATED)));

                // Adding campaign to list
                campaignList.add(campaign);
            } while (cursor.moveToNext());
        }

        return campaignList;
    }

    // Getting single Campaign
    public Campaign getCampaign(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CAMPAIGN, new String[]{KEY_CAMPAIGN_ID,
                        KEY_TITLE, KEY_TYPE, KEY_FEATURED, KEY_CAMPAIGN_JR_FORM_ID, KEY_DESCRIPTION, KEY_ICON,
                        KEY_CAMPAIGN_DATE_CREATED}, KEY_CAMPAIGN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Campaign campaign = new Campaign(
                Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_CAMPAIGN_ID))),
                cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                cursor.getString(cursor.getColumnIndex(KEY_TYPE)),
                cursor.getString(cursor.getColumnIndex(KEY_FEATURED)),
                cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndex(KEY_CAMPAIGN_JR_FORM_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_ICON)),
                cursor.getString(cursor.getColumnIndex(KEY_CAMPAIGN_DATE_CREATED)));

        return campaign;
    }

    //check if campaign exists
    public boolean isCampaignExist(Campaign campaign) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CAMPAIGN, new String[]{KEY_CAMPAIGN_ID,
                        KEY_TITLE, KEY_TYPE, KEY_FEATURED, KEY_CAMPAIGN_JR_FORM_ID, KEY_DESCRIPTION, KEY_ICON,
                        KEY_CAMPAIGN_DATE_CREATED}, KEY_CAMPAIGN_ID + "=?",
                new String[]{String.valueOf(campaign.getId())}, null, null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return (count > 0) ? true : false;
    }

    public void updateCampaign(Campaign campaign) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_TITLE, campaign.getTitle());
        values.put(KEY_TYPE, campaign.getType());
        values.put(KEY_FEATURED, campaign.getFeatured());
        values.put(KEY_CAMPAIGN_JR_FORM_ID, campaign.getJrFormId());
        values.put(KEY_ICON, campaign.getIcon());
        values.put(KEY_DESCRIPTION, campaign.getDescription());
        values.put(KEY_CAMPAIGN_DATE_CREATED, campaign.getDateCreated());

        db.update(TABLE_CAMPAIGN, values,
                KEY_CAMPAIGN_ID + " = " + campaign.getId(), null);
    }

    // Getting feedback Count
    public int getCampaignCount() {
        int count = 0;

        String countQuery = "SELECT  * FROM " + TABLE_CAMPAIGN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        if (cursor != null && !cursor.isClosed()) {
            count = cursor.getCount();
            cursor.close();
        }
        // return count
        return count;
    }


    /**
     * All CRUD(Create, Read, Update, Delete) Operations for OHKR Disease
     */

    // Adding new Disease
    public void addDisease(Disease disease) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DISEASE_ID, disease.getId());
        values.put(KEY_DISEASE_TITLE, disease.getTitle());
        values.put(KEY_DISEASE_DESCRIPTION, disease.getDescription());
        values.put(KEY_DISEASE_CAUSES, disease.getCauses());
        values.put(KEY_DISEASE_SYMPTOMS, disease.getSymptoms());
        values.put(KEY_DISEASE_DIAGNOSIS, disease.getDiagnosis());
        values.put(KEY_DISEASE_TREATMENT, disease.getTreatment());
        values.put(KEY_SPECIE_TITLE, disease.getSpecie_title());

        // Inserting Row
        db.insert(TABLE_OHKR_DISEASE, null, values);
        db.close();
    }


    // Getting All Disease
    public List<Disease> getAllDisease() {

        List<Disease> diseasesList = new ArrayList<Disease>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_OHKR_DISEASE + " ORDER BY " + KEY_DISEASE_TITLE + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Disease disease = new Disease();
                disease.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_DISEASE_ID))));
                disease.setTitle(cursor.getString(cursor.getColumnIndex(KEY_DISEASE_TITLE)));
                disease.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DISEASE_DESCRIPTION)));
                disease.setCauses(cursor.getString(cursor.getColumnIndex(KEY_DISEASE_CAUSES)));
                disease.setSymptoms(cursor.getString(cursor.getColumnIndex(KEY_DISEASE_SYMPTOMS)));
                disease.setDiagnosis(cursor.getString(cursor.getColumnIndex(KEY_DISEASE_DIAGNOSIS)));
                disease.setTreatment(cursor.getString(cursor.getColumnIndex(KEY_DISEASE_TREATMENT)));
                disease.setSpecie_title(cursor.getString(cursor.getColumnIndex(KEY_SPECIE_TITLE)));

                // Adding disease to list
                diseasesList.add(disease);
            } while (cursor.moveToNext());
        }

        return diseasesList;
    }

    // Getting single Disease
    public Disease getDisease(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_OHKR_DISEASE, new String[]{KEY_DISEASE_ID,
                        KEY_DISEASE_TITLE, KEY_DISEASE_DESCRIPTION, KEY_DISEASE_CAUSES, KEY_DISEASE_SYMPTOMS,
                        KEY_DISEASE_DIAGNOSIS, KEY_DISEASE_TREATMENT, KEY_SPECIE_TITLE}, KEY_DISEASE_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Disease disease = new Disease(
                Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_DISEASE_ID))),
                cursor.getString(cursor.getColumnIndex(KEY_DISEASE_TITLE)),
                cursor.getString(cursor.getColumnIndex(KEY_DISEASE_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndex(KEY_DISEASE_CAUSES)),
                cursor.getString(cursor.getColumnIndex(KEY_DISEASE_SYMPTOMS)),
                cursor.getString(cursor.getColumnIndex(KEY_DISEASE_DIAGNOSIS)),
                cursor.getString(cursor.getColumnIndex(KEY_DISEASE_TREATMENT)),
                cursor.getString(cursor.getColumnIndex(KEY_SPECIE_TITLE))
        );

        return disease;
    }

    //check if disease exists
    public boolean isDiseaseExist(Disease disease) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_OHKR_DISEASE, new String[]{KEY_DISEASE_ID,
                        KEY_DISEASE_TITLE, KEY_DISEASE_DESCRIPTION, KEY_DISEASE_CAUSES, KEY_DISEASE_SYMPTOMS,
                        KEY_DISEASE_DIAGNOSIS, KEY_DISEASE_TREATMENT, KEY_SPECIE_TITLE}, KEY_DISEASE_ID + "=?",
                new String[]{String.valueOf(disease.getId())}, null, null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return (count > 0) ? true : false;
    }

    public void updateDisease(Disease disease) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DISEASE_TITLE, disease.getTitle());
        values.put(KEY_DISEASE_DESCRIPTION, disease.getDescription());
        values.put(KEY_DISEASE_CAUSES, disease.getCauses());
        values.put(KEY_DISEASE_SYMPTOMS, disease.getSymptoms());
        values.put(KEY_DISEASE_DIAGNOSIS, disease.getDiagnosis());
        values.put(KEY_DISEASE_TREATMENT, disease.getTreatment());
        values.put(KEY_SPECIE_TITLE, disease.getSpecie_title());

        db.update(TABLE_OHKR_DISEASE, values,
                KEY_DISEASE_ID + " = " + disease.getId(), null);
    }


    /**
     * All CRUD(Create, Read, Update, Delete) Operations for OHKR Glossary
     */

    // Adding new Disease
    public void addGlossary(Glossary glossary) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_GLOSSARY_ID, glossary.getId());
        values.put(KEY_GLOSSARY_TITLE, glossary.getTitle());
        values.put(KEY_GLOSSARY_CODE, glossary.getCode());
        values.put(KEY_GLOSSARY_DESCRIPTION, glossary.getDescription());

        // Inserting Row
        db.insert(TABLE_OHKR_GLOSSARY, null, values);
        db.close();
    }


    // Getting All glossary
    public List<Glossary> getAllGlossary() {

        List<Glossary> glossaryList = new ArrayList<Glossary>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_OHKR_GLOSSARY + " ORDER BY " + KEY_GLOSSARY_TITLE + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Glossary glossary = new Glossary();
                glossary.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_GLOSSARY_ID))));
                glossary.setTitle(cursor.getString(cursor.getColumnIndex(KEY_GLOSSARY_TITLE)));
                glossary.setCode(cursor.getString(cursor.getColumnIndex(KEY_GLOSSARY_CODE)));
                glossary.setDescription(cursor.getString(cursor.getColumnIndex(KEY_GLOSSARY_DESCRIPTION)));

                // Adding glossary to list
                glossaryList.add(glossary);
            } while (cursor.moveToNext());
        }
        return glossaryList;
    }


    //check if glossary exists
    public boolean isGlossaryExist(Glossary glossary) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_OHKR_GLOSSARY, new String[]{KEY_GLOSSARY_ID,
                        KEY_GLOSSARY_TITLE, KEY_GLOSSARY_CODE, KEY_DISEASE_DESCRIPTION}, KEY_GLOSSARY_ID + "=?",
                new String[]{String.valueOf(glossary.getId())}, null, null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return (count > 0) ? true : false;
    }

    public void updateGlossary(Glossary glossary) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_GLOSSARY_TITLE, glossary.getTitle());
        values.put(KEY_GLOSSARY_CODE, glossary.getCode());
        values.put(KEY_GLOSSARY_DESCRIPTION, glossary.getDescription());

        db.update(TABLE_OHKR_GLOSSARY, values,
                KEY_GLOSSARY_ID + " = " + glossary.getId(), null);
    }


    /**
     * All CRUD(Create, Read, Update, Delete) Operations for Form Details
     */

    // Adding new form details
    public void addFormDetails(FormDetails forms) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FORM_DETAILS_ID, forms.getId());
        values.put(KEY_FORM_DETAILS_LABEL, forms.getLabel());
        values.put(KEY_FORM_DETAILS_TYPE, forms.getType());
        values.put(KEY_FORM_DETAILS_VALUE, forms.getValue());
        values.put(KEY_FORM_DETAILS_INSTANCE_ID, forms.getInstanceId());

        // Inserting Row
        db.insert(TABLE_FORM_DETAILS, null, values);
        db.close();
    }


    //check if form details
    public boolean isFormDetailsExist(FormDetails forms) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FORM_DETAILS, new String[]{KEY_FORM_DETAILS_ID,
                        KEY_FORM_DETAILS_LABEL, KEY_FORM_DETAILS_TYPE, KEY_FORM_DETAILS_VALUE, KEY_FORM_DETAILS_INSTANCE_ID},
                KEY_FORM_DETAILS_ID + "=?", new String[]{String.valueOf(forms.getId())}, null, null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return (count > 0) ? true : false;
    }


    //Update form details
    public void updateFormDetails(FormDetails forms) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_FORM_DETAILS_ID, forms.getId());
        values.put(KEY_FORM_DETAILS_LABEL, forms.getLabel());
        values.put(KEY_FORM_DETAILS_TYPE, forms.getType());
        values.put(KEY_FORM_DETAILS_VALUE, forms.getValue());
        values.put(KEY_FORM_DETAILS_INSTANCE_ID, forms.getInstanceId());

        db.update(TABLE_FORM_DETAILS, values, KEY_FORM_DETAILS_ID + " =" + forms.getId(), null);
    }

    // Getting Form Details
    public List<FormDetails> getFormDetails(String instanceId) {

        List<FormDetails> formList = new ArrayList<FormDetails>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FORM_DETAILS + " WHERE "
                + KEY_FORM_DETAILS_INSTANCE_ID + " = ?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{instanceId});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                FormDetails formDetails = new FormDetails();
                formDetails.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_FORM_DETAILS_ID))));
                formDetails.setLabel(cursor.getString(cursor.getColumnIndex(KEY_FORM_DETAILS_LABEL)));
                formDetails.setType(cursor.getString(cursor.getColumnIndex(KEY_FORM_DETAILS_TYPE)));
                formDetails.setValue(cursor.getString(cursor.getColumnIndex(KEY_FORM_DETAILS_VALUE)));
                formDetails.setInstanceId(cursor.getString(cursor.getColumnIndex(KEY_FORM_DETAILS_INSTANCE_ID)));
                // Adding formDetails to list
                formList.add(formDetails);
            } while (cursor.moveToNext());
        }

        // return formDetails list
        return formList;
    }

}


