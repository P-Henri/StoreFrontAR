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

public class StoresNearListActivity extends AppCompatActivity {
    public int x = 0;
    private List<Store> stores;
    Random rnd = new Random();
    private List<Product> products;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private ProgressBar spinner;


    String usersAddress;
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
    Purpose: Obtain Places from Coordinates.
    Date: November 10th 2018 - updated 11/14/2018
    */
    private void ObtainListOfLocations() {
        String getCordsURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lng + "&radius=50000&type=furniture_store&key=" + getResources().getString(R.string.placesKey);

        RequestParams rp = new RequestParams();
        HttpUtils.getByUrl(getCordsURL, rp, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("asd", "---------------- this is response : " + response);
                try {
                    double rating = 0.0;
                    String photoref = "";
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
                        stores.add(new Store(element.getJSONObject(i).get("name").toString(), element.getJSONObject(i).get("vicinity").toString(), (float) rating, element.getJSONObject(i).getString("reference"), photoref,products.get(rnd.nextInt(products.size()))));
                    }
                    //stores.sort(); //sort by rating pls
                    recyclerAdapter = new RecyclerAdapter(stores);
                    recyclerView.setAdapter(recyclerAdapter);
                    spinner.setVisibility(View.GONE);
                    ObtainExtras();
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
    private void ObtainExtras() {
        RequestParams rp = new RequestParams();
        for (int i = 0; i < stores.size(); ++i) {
            Log.d("aids", "achieved");
            x = i;
            String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + stores.get(i).reference + "&key=" + getResources().getString(R.string.placesKey);
            HttpUtils.getByUrl(url, rp, new JsonHttpResponseHandler() {
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
                        stores.get(x).SetExtras(website, phoneno);
                        Log.d("asd", "---------------- this is response : " + stores.get(x).websiteUrl);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
