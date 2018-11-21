package com.storefront.huxley.henri.storefrontar;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


/*
Developer: Evan Yohnicki-Huxley & Patrick Henri
Purpose: Used to make web calls and return json data.
Date: November 16th 2018
*/

public class HttpClient {

    private static AsyncHttpClient httpClient = new AsyncHttpClient();

    public static void obtainFromUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        httpClient.get(url, params, responseHandler);
    }

}