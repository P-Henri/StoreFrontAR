/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.storefront.huxley.henri.storefrontar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Vector;

import cz.msebera.android.httpclient.Header;

public class StoresNearActivity extends AppCompatActivity {

    LinearLayout ll;
    Vector<FoundStores> allStores = new Vector<FoundStores>();
    String placesKey = "INPUT KEY";
    String usersAddress;
    double lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storesnear);
        ll = (LinearLayout)findViewById(R.id.ll);
        try {
            usersAddress = Objects.requireNonNull(getIntent().getExtras()).getString("address");
        }
        catch (Exception e) {
        onBackPressed();
        }

        ObtainCoords();
    }

    /*
    Developer: Evan Yohnicki-Huxley
    Purpose: Obtain User Coordinates using Place API. Once Found and Obtained Obtain the List of Locations
    Date: November 10th 2018
    */
    //////////////INCOMPLETE ADD PROPER ERROR HANDLING
    private void ObtainCoords()
    {
        String getCordsURL = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=" + usersAddress + "&inputtype=textquery&fields=geometry&key=" + placesKey;
        //Toast.makeText(this, getCordsURL, Toast.LENGTH_SHORT).show();

        RequestParams rp = new RequestParams();
        //rp.add("username", "aaa"); rp.add("password", "aaa@123");

        HttpUtils.getByUrl(getCordsURL, rp, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("asd", "---------------- this is response : " + response);
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    JSONObject element = serverResp.getJSONArray("candidates").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                    lat = element.getDouble("lat");
                    lng = element.getDouble("lng");
                    ObtainListOfLocations();
                } catch (JSONException e) {
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    /*
    Developer: Evan Yohnicki-Huxley
    Purpose: Obtain Places from Coordinates. Once Found Place in Vector of Object followed by Print the list to the user
    Date: November 10th 2018
    */
    //////////////INCOMPLETE ADD PROPER ERROR HANDLING
    private void ObtainListOfLocations()
    {
        String getCordsURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lng + "&radius=1500&type=furniture_store&key=" + placesKey;
        Toast.makeText(this, getCordsURL, Toast.LENGTH_SHORT).show();

        RequestParams rp = new RequestParams();
        HttpUtils.getByUrl(getCordsURL, rp, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                FoundStores FS = null;
                Log.d("asd", "---------------- this is response : " + response);
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    //JSONObject element = serverResp.getJSONArray("candidates").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                    JSONArray element = serverResp.getJSONArray("results");
                    for(int i=0;i<=element.length()-1;++i)
                    {
                        FS = new FoundStores();
                        FS.StoreName = element.getJSONObject(i).get("name").toString();
                        FS.OpenNow = (Boolean) element.getJSONObject(i).getJSONObject("opening_hours").get("open_now");
                        FS.StoreAddress = element.getJSONObject(i).get("vicinity").toString();
                        try {
                            FS.StoreRating = (Double)element.getJSONObject(i).get("rating");
                        }
                       catch (Exception e){
                            FS.StoreRating = 0.0;
                        }
                        allStores.add(FS);
                    }
                    ListToObject();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    /*
    Developer: Evan Yohnicki-Huxley
    Purpose: Print the list to the user
    Date: November 10th 2018
    */
    private void ListToObject()
    {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        for(FoundStores fs : allStores){
            TextView tv = new TextView(this);
            tv.setText(fs.StoreName);
            linearLayout.addView(tv);
        }
        ll.addView(linearLayout);
    }
};
