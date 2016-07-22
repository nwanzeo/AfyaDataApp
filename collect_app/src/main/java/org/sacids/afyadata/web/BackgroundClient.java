package org.sacids.afyadata.web;

import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import java.net.URL;

/**
 * Created by Renfrid-Sacids on 3/16/2016.
 */
public class BackgroundClient {
    private static SyncHttpClient client = new SyncHttpClient();
    private URL urlObject;


    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }


    private static String getAbsoluteUrl(String relativeUrl) {
        Log.d("Rest client", "Request page => " + relativeUrl);
        return relativeUrl;
    }
}
