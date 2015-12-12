package com.martingold.nfcreader.Utils;

/**
 * Created by martin on 21.10.15.
 */
import android.util.Log;

import com.loopj.android.http.*;

public class RestClient {
    private static final String BASE_URL = Constants.server+"api/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        String URL = Constants.server+"/api/"+ relativeUrl;
        Log.i("nfc", URL);
        return URL;
    }
}