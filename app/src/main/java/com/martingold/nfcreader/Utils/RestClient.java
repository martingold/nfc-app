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
        Log.i("nfc", getAbsoluteUrl(url));
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}