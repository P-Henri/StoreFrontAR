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

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class StoresNearListActivity extends AppCompatActivity {
    private List<Store> stores;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private ProgressBar spinner;

    String placesKey = "AIzaSyCNn3rbBz-ERQp_rH4QkCou4E5wYLl7XV0";
    String usersAddress;
    double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storesnearlist);

        try {
            usersAddress = Objects.requireNonNull(getIntent().getExtras()).getString("address");
        } catch (Exception e) {
            onBackPressed();
        }

        stores = new ArrayList<>();
        spinner = findViewById(R.id.progressBar1);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        spinner.setVisibility(View.VISIBLE);

        ObtainCoords();
    }

    /*
    Developer: Evan Yohnicki-Huxley
    Purpose: Obtain User Coordinates using Place API. Once Found and Obtained Obtain the List of Locations
    Date: November 10th 2018
    */
    //////////////INCOMPLETE ADD PROPER ERROR HANDLING
    private void ObtainCoords() {
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
    Developer: Evan Yohnicki-Huxley & Patrick Henri
    Purpose: Obtain Places from Coordinates.
    Date: November 10th 2018 - updated 11/14/2018
    */
    //////////////INCOMPLETE ADD PROPER ERROR HANDLING
    private void ObtainListOfLocations() {
        String getCordsURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lng + "&radius=10000&type=furniture_store&key=" + placesKey;
        //Toast.makeText(this, getCordsURL, Toast.LENGTH_SHORT).show();

        RequestParams rp = new RequestParams();
        HttpUtils.getByUrl(getCordsURL, rp, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("asd", "---------------- this is response : " + response);
                try {
                    double rating = 0.0;
                    JSONObject serverResp = new JSONObject(response.toString());
                    //JSONObject element = serverResp.getJSONArray("candidates").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                    JSONArray element = serverResp.getJSONArray("results");
                    for (int i = 0; i <= element.length() - 1; ++i) {
                        if(element.getJSONObject(i).has("rating")) {
                           rating = element.getJSONObject(i).getDouble("rating");
                        }
                        stores.add(new Store(element.getJSONObject(i).get("name").toString(), element.getJSONObject(i).get("vicinity").toString(), (float)rating));
                        Log.d("store", "-----loop result add: " + stores.get(i).name);
                    }
                    //stores.sort(); //sort by rating pls
                    recyclerAdapter = new RecyclerAdapter(stores);
                    recyclerView.setAdapter(recyclerAdapter);
                    spinner.setVisibility(View.GONE);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }
}
