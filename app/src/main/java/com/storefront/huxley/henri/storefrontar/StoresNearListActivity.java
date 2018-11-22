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

import java.util.ArrayList;
import java.util.List;
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
        //Description from https://www.ikea.lv/en/products/kitchen/dining-seating/chairs-and-benches/industriell-bench-light-grey-art-30394538
        products.add(new Product("Bench","Only for indoor use. Seats 2-3.This bench has been tested for domestic use and meets the requirements for durability and safety, set forth in the following standards: EN 12520, EN 1728 and EN 1022. For increased stability, re-tighten the screws about two weeks after assembly and when necessary. May be completed with FIXA stick-on floor protectors; protect the underlying surface against wear.","bench","$212.99"));
        //Description from https://www.ikea.lv/en/products/kitchen/dining-tables/tables/melltorp-table-white-spr-89280035
        products.add(new Product("Coffee Table","MELLTORP table has four legs, a melamine top (which shrugs off coffee or red wine spills) and it’s designed to last for years of busy everyday life. The part where it stands out is its price tag. To make MELLTORP more affordable we make the table top and legs separately, then ship them directly to the store, cutting out any extra production steps that could add to the price. There are two boxes to pick up, but at a price that leaves money over for well, maybe a nice dinner.","coffeetable","$122.99"));
        //Description from https://www.ikea.lv/en/products/bedroom/sofa-armchairs/sofas/vimle-1-seat-section-beige-spr-59253260
        products.add(new Product("Modern Sectional Couch","All the cotton in our products comes from more sustainable sources. This means that the cotton is either recycled, or grown with less water, less fertilisers and less pesticides, while increasing profit margins for the farmers.","couch","$1202.99"));
        //Description from https://www.ikea.lv/en/products/bedroom/bedside-tables/bedside-tables/tyssedal-bedside-table-white-art-70299959
        products.add(new Product("Wooden Bed Side Table","Light, neat and soft – with a handcrafted feeling in every detail. That’s a short description of the TYSSEDAL bedroom series. But there’s much more we’d like to tell you about. Like the fact that the sunlight can filter through the headboard, the wardrobe has mirrored doors and the chests of drawers and bedside table have softly shaped tops and drawer bottoms with a printed pattern. All the pieces are on turned legs and have handles in brushed chrome. TYSSEDAL is a complete bedroom series with quality and style that will last for many years.","eb_nightstand_01","$172.99"));
        //Description from https://www.ikea.lv/en/products/dining-room/dining-seating/garden-chairs-benches-loungers/sjalland-table-outdoor-light-grey-light-brown-spr-79262447
        products.add(new Product("Wooden Dinning Room Table","The table top in wood is made from eucalyptus slats, all with grain variations and natural colour shifts that give the table a warm and natural look. You can choose between table tops in different materials, depending on what suits you and your patio ‒ and if you prefer a maintenance-free material like aluminum or the warmth of durable eucalyptus.","table","$319.99"));
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
                    /* No Stores Found Fix */
                    if(stores.size() == 0) {
                        Toast.makeText(StoresNearListActivity.this,"No Stores Found Near You!", Toast.LENGTH_LONG).show();
                        onBackPressed();
                        return;
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
