package org.odk.collect.android.database;

import android.app.Activity;
import android.database.Cursor;

import org.odk.collect.android.provider.InstanceProviderAPI;

/**
 * Created by Renfrid-Sacids on 3/16/2016.
 */
public class DataSource {

    private Activity _activity;

    public DataSource(Activity a) {
        this._activity = a;
    }

    public Cursor getUnsentCursor() {
        // get all complete or failed submission instances
        String selection = InstanceProviderAPI.InstanceColumns.STATUS + "=? or "
                + InstanceProviderAPI.InstanceColumns.STATUS + "=?";
        String selectionArgs[] = {InstanceProviderAPI.STATUS_COMPLETE,
                InstanceProviderAPI.STATUS_SUBMISSION_FAILED};
        String sortOrder = InstanceProviderAPI.InstanceColumns.DISPLAY_NAME + " ASC";
        Cursor c = _activity.managedQuery(InstanceProviderAPI.InstanceColumns.CONTENT_URI, null, selection,
                selectionArgs, sortOrder);
        return c;
    }

    public Cursor getAllCursor() {
        // get all complete or failed submission instances
        String selection = InstanceProviderAPI.InstanceColumns.STATUS + "=? or "
                + InstanceProviderAPI.InstanceColumns.STATUS + "=? or " + InstanceProviderAPI.InstanceColumns.STATUS
                + "=?";
        String selectionArgs[] = {InstanceProviderAPI.STATUS_COMPLETE,
                InstanceProviderAPI.STATUS_SUBMISSION_FAILED,
                InstanceProviderAPI.STATUS_SUBMITTED};
        String sortOrder = InstanceProviderAPI.InstanceColumns.DISPLAY_NAME + " ASC";
        Cursor c = _activity.managedQuery(InstanceProviderAPI.InstanceColumns.CONTENT_URI, null, selection,
                selectionArgs, sortOrder);
        return c;
    }

    public Cursor getSavedFormsToEdit() {
        String selection = InstanceProviderAPI.InstanceColumns.STATUS + " != ?";
        String[] selectionArgs = {InstanceProviderAPI.STATUS_SUBMITTED};
        String sortOrder = InstanceProviderAPI.InstanceColumns.STATUS + " DESC, " + InstanceProviderAPI.InstanceColumns.DISPLAY_NAME + " ASC";
        Cursor c = _activity.managedQuery(InstanceProviderAPI.InstanceColumns.CONTENT_URI, null, selection, selectionArgs, sortOrder);
        return c;
    }

}
