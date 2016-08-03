package org.sacids.afyadata.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.sacids.afyadata.tasks.DownloadFeedback;

/**
 * Created by Renfrid-Sacids on 6/23/2016.
 */
public class FeedbackReceiver extends BroadcastReceiver {
    public FeedbackReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Invoke background service to process data
        Intent service = new Intent(context, DownloadFeedback.class);
        context.startService(service);
    }
}
