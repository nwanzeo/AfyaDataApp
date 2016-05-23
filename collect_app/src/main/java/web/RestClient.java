package web;

import android.net.Uri;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;

import java.net.URL;

/**
 * Created by Renfrid-Sacids on 3/16/2016.
 */
public class RestClient {
    private static AsyncHttpClient client = new AsyncHttpClient();
    private URL urlObject;


    public static void get(String username, String password, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {

        Uri u = Uri.parse(url);
        client.clearCredentialsProvider();
        client.setCredentials(
                new AuthScope(u.getHost(), u.getPort() == -1 ? 80 : u.getPort()),
                new UsernamePasswordCredentials(
                        username,
                        password
                )
        );
        client.setAuthenticationPreemptive(true);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params,
                            AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        Log.d("Rest client", "Request page => " + relativeUrl);
        return relativeUrl;
    }
}


