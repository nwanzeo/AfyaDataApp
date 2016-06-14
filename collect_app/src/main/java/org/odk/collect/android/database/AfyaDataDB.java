package org.odk.collect.android.database;

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

import org.odk.collect.android.models.Campaign;
import org.odk.collect.android.models.Disease;
import org.odk.collect.android.models.Feedback;
import org.odk.collect.android.picasa.Feed;

public class AfyaDataDB extends SQLiteOpenHelper {

    private static final String TAG = AfyaDataDB.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "afyadata.db";

    // Feedback table
    public static final String TABLE_FEEDBACK = "feedback";
    public static final String KEY_FEEDBACK_ID = "id";
    public static final String KEY_FEEDBACK_FORM_ID = "form_id";
    public static final String KEY_INSTANCE_ID = "instance_id";
    public static final String KEY_FORM_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_SENDER = "sender";
    public static final String KEY_USER = "user";
    public static final String KEY_DATE_CREATED = "date_created";
    public static final String KEY_FEEDBACK_STATUS = "status";

    //Campaign table
    public static final String TABLE_CAMPAIGN = "campaign";
    public static final String KEY_CAMPAIGN_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_TYPE = "type";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_ICON = "icon";
    public static final String KEY_CAMPAIGN_FORM_ID = "form_id";
    public static final String KEY_CAMPAIGN_DATE_CREATED = "date_created";

    //OHKR disease table
    public static final String TABLE_OHKR_DISEASE = "ohkr_disease";
    public static final String KEY_DISEASE_ID = "id";
    public static final String KEY_DISEASE_TITLE = "title";
    public static final String KEY_DISEASE_DESCRIPTION = "description";
    public static final String KEY_SPECIE_TITLE = "specie_title";


    public AfyaDataDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FEEDBACK_TABLE = "CREATE TABLE "
                + TABLE_FEEDBACK + "("
                + KEY_FEEDBACK_ID + " INTEGER PRIMARY KEY,"
                + KEY_FEEDBACK_FORM_ID + " TEXT,"
                + KEY_INSTANCE_ID + " TEXT,"
                + KEY_FORM_TITLE + " TEXT,"
                + KEY_MESSAGE + " TEXT,"
                + KEY_SENDER + " TEXT,"
                + KEY_USER + " TEXT,"
                + KEY_DATE_CREATED + " TEXT,"
                + KEY_FEEDBACK_STATUS + " TEXT" + ")";

        String CREATE_CAMPAIGN_TABLE = "CREATE TABLE "
                + TABLE_CAMPAIGN + "("
                + KEY_CAMPAIGN_ID + " INTEGER PRIMARY KEY," // and auto increment will be handled with
                + KEY_TITLE + " TEXT,"
                + KEY_TYPE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_ICON + " TEXT,"
                + KEY_CAMPAIGN_FORM_ID + " TEXT,"
                + KEY_CAMPAIGN_DATE_CREATED + " TEXT" + ")";

        String CREATE_DISEASE_TABLE = "CREATE TABLE "
                + TABLE_OHKR_DISEASE + "("
                + KEY_DISEASE_ID + " INTEGER PRIMARY KEY," // and auto increment will be handled with
                + KEY_DISEASE_TITLE + " TEXT,"
                + KEY_DISEASE_DESCRIPTION + " TEXT,"
                + KEY_SPECIE_TITLE + " TEXT" + ")";


        db.execSQL(CREATE_FEEDBACK_TABLE);
        db.execSQL(CREATE_CAMPAIGN_TABLE);
        db.execSQL(CREATE_DISEASE_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDBACK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAMPAIGN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OHKR_DISEASE);

        // Create tables again
        onCreate(db);
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
        values.put(KEY_DATE_CREATED, feedback.getDateCreated());
        values.put(KEY_FEEDBACK_STATUS, feedback.getStatus());

        // Inserting Row
        db.insert(TABLE_FEEDBACK, null, values);
        db.close(); // Closing database connection
    }

    // Getting All Feedback
    public List<Feedback> getAllFeedback() {

        List<Feedback> feedbackList = new ArrayList<Feedback>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FEEDBACK + " GROUP BY " + KEY_INSTANCE_ID;

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
                feedback.setDateCreated(cursor.getString(cursor.getColumnIndex(KEY_DATE_CREATED)));
                feedback.setStatus(cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_STATUS)));

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
        Cursor cursor = db.rawQuery(selectQuery, new String[] {instanceId});

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
                feedback.setDateCreated(cursor.getString(cursor.getColumnIndex(KEY_DATE_CREATED)));
                feedback.setStatus(cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_STATUS)));

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
                        KEY_DATE_CREATED, KEY_FEEDBACK_STATUS},
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
        values.put(KEY_DATE_CREATED, feedback.getDateCreated());
        values.put(KEY_FEEDBACK_STATUS, feedback.getStatus());

        // updating row
        return db.update(TABLE_FEEDBACK, values, KEY_FEEDBACK_ID + " = ?",
                new String[]{String.valueOf(feedback.getId())});
    }

    // Deleting single feedback
    public void deleteFeedback(Feedback feedback) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FEEDBACK, KEY_FEEDBACK_ID + " = ?",
                new String[]{String.valueOf(feedback.getId())});
        db.close();
    }

    // Getting feedback Count
    public int getFeedbackCount() {
        int count = 0;
        String countQuery = "SELECT  * FROM " + TABLE_FEEDBACK;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        if(cursor != null && !cursor.isClosed()){
            count = cursor.getCount();
            cursor.close();
        }

        // return count
        return count;
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
        values.put(KEY_CAMPAIGN_FORM_ID, campaign.getFormId());
        values.put(KEY_ICON, campaign.getIcon());
        values.put(KEY_DESCRIPTION, campaign.getDescription());
        values.put(KEY_CAMPAIGN_DATE_CREATED, campaign.getDateCreated());

        // Inserting Row
        db.insert(TABLE_CAMPAIGN, null, values);
        db.close();
    }


    // Getting All Campaign
    public List<Campaign> getAllCampaign() {

        List<Campaign> campaignList = new ArrayList<Campaign>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CAMPAIGN;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Campaign campaign = new Campaign();
                campaign.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_CAMPAIGN_ID))));
                campaign.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                campaign.setType(cursor.getString(cursor.getColumnIndex(KEY_TYPE)));
                campaign.setFormId(cursor.getString(cursor.getColumnIndex(KEY_CAMPAIGN_FORM_ID)));
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
                        KEY_TITLE, KEY_TYPE, KEY_CAMPAIGN_FORM_ID, KEY_DESCRIPTION, KEY_ICON,
                        KEY_CAMPAIGN_DATE_CREATED}, KEY_CAMPAIGN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Campaign campaign = new Campaign(
                Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_CAMPAIGN_ID))),
                cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                cursor.getString(cursor.getColumnIndex(KEY_TYPE)),
                cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndex(KEY_CAMPAIGN_FORM_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_ICON)),
                cursor.getString(cursor.getColumnIndex(KEY_CAMPAIGN_DATE_CREATED)));

        return campaign;
    }

    //check if campaign exists
    public boolean isCampaignExist(Campaign campaign) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CAMPAIGN, new String[]{KEY_CAMPAIGN_ID,
                        KEY_TITLE, KEY_TYPE, KEY_CAMPAIGN_FORM_ID, KEY_DESCRIPTION, KEY_ICON,
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
        values.put(KEY_CAMPAIGN_FORM_ID, campaign.getFormId());
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

        if(cursor != null && !cursor.isClosed()){
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
        values.put(KEY_SPECIE_TITLE, disease.getSpecie_title());

        // Inserting Row
        db.insert(TABLE_OHKR_DISEASE, null, values);
        db.close();
    }


    // Getting All Disease
    public List<Disease> getAllDisease() {

        List<Disease> diseasesList = new ArrayList<Disease>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_OHKR_DISEASE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Disease disease = new Disease();
                disease.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_DISEASE_ID))));
                disease.setTitle(cursor.getString(cursor.getColumnIndex(KEY_DISEASE_TITLE)));
                disease.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DISEASE_DESCRIPTION)));
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
                        KEY_DISEASE_TITLE, KEY_DISEASE_DESCRIPTION, KEY_SPECIE_TITLE}, KEY_DISEASE_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Disease disease = new Disease(
                Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_DISEASE_ID))),
                cursor.getString(cursor.getColumnIndex(KEY_DISEASE_TITLE)),
                cursor.getString(cursor.getColumnIndex(KEY_DISEASE_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndex(KEY_SPECIE_TITLE))
        );

        return disease;
    }

    //check if campaign exists
    public boolean isDiseaseExist(Disease disease) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_OHKR_DISEASE, new String[]{KEY_DISEASE_ID,
                        KEY_DISEASE_TITLE, KEY_DISEASE_DESCRIPTION, KEY_SPECIE_TITLE}, KEY_DISEASE_ID + "=?",
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
        values.put(KEY_SPECIE_TITLE, disease.getSpecie_title());

        db.update(TABLE_OHKR_DISEASE, values,
                KEY_DISEASE_ID + " = " + disease.getId(), null);
    }


}


