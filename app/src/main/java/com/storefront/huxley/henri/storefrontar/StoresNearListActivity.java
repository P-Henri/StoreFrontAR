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
import org.xml.sax.Parser;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

/*
Developer: Evan Yohnicki-Huxley & Patrick Henri
Purpose: Activity Storing all the near stores
Date: November 12th 2018
*/

public class StoresNearListActivity extends AppCompatActivity {
    private List<Store> stores;
    private List<Product> products;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private ProgressBar spinner;
    private Random rnd = new Random();
    double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storesnearlist);
        addProducts();

        lat = (getIntent().getExtras()).getDouble("lat");
        lng = (getIntent().getExtras()).getDouble("lng");

        stores = new ArrayList<>();
        spinner = findViewById(R.id.progressBar1);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        spinner.setVisibility(View.VISIBLE);

        ObtainListOfLocations();
    }

    /*
    Developer: Evan Yohnicki-Huxley
    Purpose: Adds Products ot an array list to be added randomly selected for a store
    Date: November 16th 2018
    */
    private void addProducts()
    {
        products = new ArrayList<Product>();
        products.add(new Product("Bench","A Bench made from quality walnut wood.","bench","$212.99"));
        products.add(new Product("Coffee Table","A modern take on a required peice of furniture for all homes. Great to wow your guests with your sophisticated style.","coffeetable","$122.99"));
        products.add(new Product("Modern Sectional Couch","A beautiful white sectional couch to show off your style and eye for quality furniture.","couch","$1202.99"));
        products.add(new Product("Wooden Bed Side Table","A small and basic wooden bedside table with metal handles","eb_nightstand_01","$172.99"));
        products.add(new Product("Wooden Dinning Room Table","A perfect standard wooden table for a large families looking to get together around the table.","table","$319.99"));
    }

    /*
    Developer: Evan Yohnicki-Huxley & Patrick Henri
    Purpose: Obtain Places based on the users Coordinates.
    Date: November 10th 2018 - updated 11/14/2018
    */
    private void ObtainListOfLocations() {
        String getCordsURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lng + "&radius=50000&type=furniture_store&key=" + getResources().getString(R.string.placesKey);

        RequestParams rp = new RequestParams();
        HttpClient.obtainFromUrl(getCordsURL, rp, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("asd", "---------------- this is response : " + response);
                try {
                    double rating = 0.0;
                    String photoref = "";
                    String name = "";
                    JSONObject serverResp = new JSONObject(response.toString());
                    //JSONObject element = serverResp.getJSONArray("candidates").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                    JSONArray element = serverResp.getJSONArray("results");
                    for (int i = 0; i <= element.length() - 1; ++i) {
                        if (element.getJSONObject(i).has("rating")) {
                            rating = element.getJSONObject(i).getDouble("rating");
                        }
                        if (element.getJSONObject(i).has("photos")) {
                            photoref = element.getJSONObject(i).getJSONArray("photos").getJSONObject(0).get("photo_reference").toString();
                        }
                        if(element.getJSONObject(i).get("name").toString().length() > 34) {
                            name = element.getJSONObject(i).get("name").toString().substring(0, 34);
                            name += "...";
                        }
                        else {
                            name = element.getJSONObject(i).get("name").toString();
                        }
                        stores.add(new Store(name, element.getJSONObject(i).get("vicinity").toString(), (float) rating, element.getJSONObject(i).getString("reference"), photoref,products.get(rnd.nextInt(products.size()))));
                    }

                    for (int i = 0; i < stores.size(); ++i) {
                        ObtainExtras(i);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    /* Developer: Patrick Henri
       Purpose: Obtains extra info for Places found earlier from Coordinates.
       Date: November 17th 2018
    */
    private void ObtainExtras(final int x) {
        RequestParams rp = new RequestParams();
        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + stores.get(x).reference + "&key=" + getResources().getString(R.string.placesKey);
        HttpClient.obtainFromUrl(url, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String phoneno = "";
                String website = "";
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    JSONObject element = serverResp.getJSONObject("result");

                    if (element.has("website")) {
                        website = element.get("website").toString();
                    }
                    if (element.has("formatted_phone_number")) {
                        phoneno = element.get("formatted_phone_number").toString();
                    }
                    Log.e("VALUE", "---------------- this is response : " + x);
                    stores.get(x).SetExtras(website, phoneno);
                    Log.e("asd", "---------------- this is response : " + stores.get(x).websiteUrl);

                    if(x == stores.size()-1) {
                        recyclerAdapter = new RecyclerAdapter(stores);
                        recyclerView.setAdapter(recyclerAdapter);
                        spinner.setVisibility(View.GONE);
                    }
                    //stores.sort(); //sort by rating pls
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }
}
