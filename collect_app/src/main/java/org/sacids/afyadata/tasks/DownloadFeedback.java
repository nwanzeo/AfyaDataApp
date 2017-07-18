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

package org.sacids.afyadata.tasks;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.sacids.afyadata.R;
import org.sacids.afyadata.activities.MainActivity;
import org.sacids.afyadata.database.AfyaDataDB;
import org.sacids.afyadata.models.Feedback;
import org.sacids.afyadata.preferences.PreferencesActivity;
import org.sacids.afyadata.prefs.Preferences;
import org.sacids.afyadata.web.BackgroundClient;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class DownloadFeedback extends IntentService {

    private static String TAG = "DownloadFeedback";

    private SharedPreferences mSharedPreferences;
    private String username;
    private String serverUrl;
    private String language;

    private AfyaDataDB db;

    // A integer, that identifies each notification uniquely
    public static final int NOTIFICATION_ID = 1;

    //variable Tag
    private static final String TAG_ID = "id";
    private static final String TAG_FORM_ID = "form_id";
    private static final String TAG_INSTANCE_ID = "instance_id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_SENDER = "sender";
    private static final String TAG_USER = "user";
    private static final String TAG_CHR_NAME = "chr_name";
    private static final String TAG_DATE_CREATED = "date_created";
    private static final String TAG_STATUS = "status";
    private static final String TAG_REPLY_BY = "reply_by";


    public DownloadFeedback() {
        super("DownloadFeedback");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            username = mSharedPreferences.getString(PreferencesActivity.KEY_USERNAME, null);
            serverUrl = mSharedPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                    getString(R.string.default_server_url));

            //TODO language request
            language = mSharedPreferences.getString(Preferences.DEFAULT_LOCALE, null);

            db = new AfyaDataDB(getApplicationContext());

            Feedback lastFeedback = db.getLastFeedback();

            String dateCreated;
            long lastId;
            if (lastFeedback != null) {
                dateCreated = lastFeedback.getDateCreated();
                lastId = lastFeedback.getId();
            } else {
                dateCreated = null;
                lastId = 0;
            }

            //params
            RequestParams param = new RequestParams();
            param.add("username", username);
            param.add("lastId", String.valueOf(lastId));
            param.add("date_created", dateCreated);
            param.add("language", language);

            String feedbackURL = serverUrl + "/api/v3/feedback/notification";

            BackgroundClient.get(feedbackURL, param, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    try {
                        if (response.getString("status").equalsIgnoreCase("success")) {
                            JSONArray feedbackArray = response.getJSONArray("feedback");

                            int feedbackNo = feedbackArray.length();

                            for (int i = 0; i < feedbackArray.length(); i++) {
                                JSONObject obj = feedbackArray.getJSONObject(i);
                                Feedback fb = new Feedback();
                                fb.setId(obj.getInt(TAG_ID));
                                fb.setFormId(obj.getString(TAG_FORM_ID));
                                fb.setInstanceId(obj.getString(TAG_INSTANCE_ID));
                                fb.setTitle(obj.getString(TAG_TITLE));
                                fb.setMessage(obj.getString(TAG_MESSAGE));
                                fb.setSender(obj.getString(TAG_SENDER));
                                fb.setUserName(obj.getString(TAG_USER));
                                fb.setDateCreated(obj.getString(TAG_DATE_CREATED));
                                fb.setStatus(obj.getString(TAG_STATUS));

                                /*if (!db.isFeedbackExist(fb)) {
                                    db.addFeedback(fb);
                                } else {
                                    db.updateFeedback(fb);
                                }*/
                            }

                            //Notification Message
                            String message = feedbackNo + " " + getResources().getString(R.string.new_feedback);
                            //send Notification
                            sendNotification(getResources().getString(R.string.app_name), message);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.d(TAG, "on Failure " + responseString);
                }
            });

        }
    }


    private void sendNotification(String title, String message) {
        // notification is selected
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.putExtra("feedback", "formFeedback");
        PendingIntent pendIntent = PendingIntent.getActivity(this, 0, notifyIntent, 0);

        // Use NotificationCompat.Builder to set up our notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        //icon appears in device notification bar and right hand corner of notification
        builder.setSmallIcon(R.drawable.ic_launcher);
        // Large icon appears on the left of the notification
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        // Content title, which appears in large type at the top of the notification
        builder.setContentTitle(title);
        // Content text, which appears in smaller text below the title
        builder.setContentText(message);
        // The subtext, which appears under the text on newer devices.
        // This will show-up in the devices with Android 4.2 and above only
        //builder.setSubText("Date: " + date_created);

        //set pending intent
        builder.setContentIntent(pendIntent).setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Will display the notification in the notification bar
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

}
