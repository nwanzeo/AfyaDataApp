package org.sacids.afyadata.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;
import org.sacids.afyadata.R;
import org.sacids.afyadata.adapters.NavDrawerListAdapter;
import org.sacids.afyadata.fragments.CampaignFragment;
import org.sacids.afyadata.fragments.FeedbackFragment;
import org.sacids.afyadata.fragments.GlossaryListFragment;
import org.sacids.afyadata.fragments.HealthTipsFragment;
import org.sacids.afyadata.fragments.LaboratoryFragment;
import org.sacids.afyadata.models.NavDrawerItem;
import org.sacids.afyadata.preferences.PrefManager;
import org.sacids.afyadata.preferences.PreferencesActivity;
import org.sacids.afyadata.prefs.Preferences;
import org.sacids.afyadata.receivers.FeedbackReceiver;
import org.sacids.afyadata.web.RestClient;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {

    private static String TAG = MainActivity.class.getSimpleName();

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    private Context context = this;
    private PackageInfo packageInfo;
    private long currentVersion;
    private long newVersion;
    private String packageName;

    SharedPreferences settings;
    private PrefManager pref;

    private SharedPreferences mSharedPreferences;
    private String serverUrl;

    private PendingIntent pendingIntent;
    private AlarmManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        serverUrl = mSharedPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        settings = getSharedPreferences(Preferences.AFYA_DATA, MODE_PRIVATE);

        pref = new PrefManager(this);

        //deal with Navigation drawer
        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Campaign
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Form Feedback
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Health Tips
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Symptoms List
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
        //Lab
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        // Change language
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
        //logout
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));

        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_navigation_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }

        //check App updates
        packageName = getPackageName();
        updateAppVersion();

        // Retrieve a PendingIntent that will perform a broadcast
        Intent feedbackIntent = new Intent(this, FeedbackReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, feedbackIntent, 0);

        // Set the alarm here.
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 1800000; // 30 minutes
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);

        // Get the message from the intent
        Intent intent = getIntent();
        // Get the extras (if there are any)
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String formFeedback = extras.getString("feedback");
            if (formFeedback.equalsIgnoreCase("formFeedback")) {
                displayView(1);
            }
        }
    }


    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

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


    /**
     * Diplaying fragment view for selected nav drawer list item
     */
    private void displayView(int position) {
        // update the main_menu content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                //campaign fragment
                fragment = new CampaignFragment();
                break;
            case 1:
                //Forms feedback
                fragment = new FeedbackFragment();
                break;
            case 2:
                //Health Tips
                fragment = new HealthTipsFragment();
                break;
            case 3:
                //Glossary List
                fragment = new GlossaryListFragment();
                break;
            case 4:
                //Lab
                fragment = new LaboratoryFragment();
                break;
            case 5:
                //change Language
                showChangeLanguageDialog();
                break;
            case 6:
                //clear session
                pref.clearSession();
                //start new Intent
                Intent signOut = new Intent(this, LoginActivity.class);
                signOut.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(signOut);
                finish();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * check app version
     */
    public void updateAppVersion() {
        String versionURL = serverUrl + "/api/v2/auth/version";
        RestClient.get(versionURL, new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(TAG, "Server response " + response.toString());
                try {
                    currentVersion = getAppVersionCode();
                    if (response.getString("status").equalsIgnoreCase("success")) {
                        JSONObject obj = response.getJSONObject("app_version");
                        newVersion = obj.getLong("version_code");

                        if (newVersion > currentVersion)
                            updateAppDialog();
                    }
                } catch (Exception e) {
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

    public void updateAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getString(R.string.update_status));
        builder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                    .parse("market://details?id="
                                            + packageName)));
                        } catch (android.content.ActivityNotFoundException appVersion) {
                            startActivity(new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id="
                                            + packageName)));
                        }
                    }
                });
        builder.setNegativeButton(getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // show number of cancel
                    }
                });
        builder.create().show();
    }

    public long getAppVersionCode() throws PackageManager.NameNotFoundException {
        packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        return packageInfo.versionCode;
    }


    /**
     * change Language dialog
     */
    private void showChangeLanguageDialog() {

        LayoutInflater li = LayoutInflater.from(context);
        View promptView = li.inflate(R.layout.dialog_change_language, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setView(promptView);

        // set dialog message
        alertDialogBuilder.setTitle(R.string.title_choose_language);
        alertDialogBuilder.setIcon(R.drawable.ic_language_black_48dp);
        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        promptView.findViewById(R.id.btn_swahili).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("sw");
                alertDialog.dismiss();
            }

        });

        promptView.findViewById(R.id.btn_english).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("en");
                alertDialog.dismiss();
            }

        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
                startActivity(getIntent());
            }
        });

        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }

    private void setLocale(String locale) {
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(locale);
        res.updateConfiguration(conf, dm);
        settings.edit().putString(Preferences.DEFAULT_LOCALE, locale).commit();
        settings.edit().putBoolean(Preferences.FIRST_TIME_APP_OPENED, false).commit();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.exit_status))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        moveTaskToBack(true);
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


}
