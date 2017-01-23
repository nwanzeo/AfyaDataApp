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
