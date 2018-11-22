package com.storefront.huxley.henri.storefrontar;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/*
Developer: Evan Yohnicki-Huxley & Patrick Henri
Purpose: Activity Showing the Store Item
Date: November 12th 2018
*/

public class StoreItemView extends AppCompatActivity {


    Store store;

    //On Create sets the items values to the the text fields
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_item_view);

        ImageView  image2;
        TextView name, address, website, phone;
        RatingBar rating;

        Store store = (Store) getIntent().getExtras().getSerializable("store_data");
        this.store = store;

        name = findViewById(R.id.storeitem_name);
        address = findViewById(R.id.storeitem_address);
        website = findViewById(R.id.storeitem_website);
        phone = findViewById(R.id.storeitem_phone);
        rating = findViewById(R.id.storeitem_rating);
        image2 = findViewById(R.id.imageView2);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        image2.setMinimumWidth(displayMetrics.widthPixels);

        //Sets the text fields some check if null, if null hide.
        String nameStr = store.name;
        if(store.name.length() > 26) {
            nameStr = store.name.substring(0, 26);
            nameStr += "...";
        }
        name.setText(nameStr);
        hideIfNull(address, store.address);
        if(store.websiteUrl != null) {
            website.setText(R.string.website_click);
        }
        else {
            website.setText("");
        }
        hideIfNull(phone, store.phoneno);

        //Set rating
        rating.setNumStars(5);
        rating.setIsIndicator(true);
        rating.setRating(store.rating);

        //Set description and cost
        ((TextView) findViewById(R.id.storeitem_description)).setText(store.selectedProduct.productDescription);
        ((TextView) findViewById(R.id.storeitem_price)).setText(getString(R.string.price_string) + store.selectedProduct.productCost);

        //setup images
        try {
            Resources resources = this.getResources();
            final int resourceId = resources.getIdentifier(store.selectedProduct.productObjectName, "drawable", this.getPackageName());
            ((ImageView) findViewById(R.id.productImg)).setImageResource(resourceId);
        }
        catch(Exception e){
            ((ImageView) findViewById(R.id.productImg)).setVisibility(View.INVISIBLE);
        }
    }


    /*
    Developer: Evan Yohnicki-Huxley
    Purpose: Rerun the onUserCoords if they accept the permission for GPS usage
    Date: November 16th 2018
    */
    public void hideIfNull(TextView txt, String value) {
        if (value == null)
            txt.setVisibility(View.INVISIBLE);
        else
            txt.setText(value);
    }

    /*
    Developer: Evan Yohnicki-Huxley
    Purpose: Opens the ar view using the object name in the product object
    Date: November 16th 2018
    */
    public void viewInAR(View view) {
        Intent intent = new Intent(this, ARCore.class);
        intent.putExtra("filename", store.selectedProduct.productObjectName + ".sfb");
        startActivity(intent);
    }


    /*
    Developer: Evan Yohnicki-Huxley
    Purpose: opens phone intent using the stores phone number
    Date: November 16th 2018
    */
    public void openPhone(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + store.phoneno));
        startActivity(intent);
    }

    /*
    Developer: Evan Yohnicki-Huxley
    Purpose: Opens the website supplied
    Date: November 16th 2018
    */
    public void openWebsite(View view) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(store.websiteUrl));
        startActivity(i);
    }

    /*
    Developer: Evan Yohnicki-Huxley
    Purpose: Will open google maps to the places location if maps is installed
    Date: November 16th 2018
    */
    public void openMaps(View view) {

        //Check if Maps is installed otherwise don't
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "To Get Directions Install Google Maps on Your Device", Toast.LENGTH_LONG).show();
            return;
        }

        //Pull Coords
        String getCordsURL = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=" + store.address + "&inputtype=textquery&fields=geometry&key=" + getResources().getString(R.string.placesKey);

            RequestParams rp = new RequestParams();
            HttpClient.obtainFromUrl(getCordsURL, rp, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    Log.d("asd", "---------------- this is response : " + response);
                    try {
                        JSONObject serverResp = new JSONObject(response.toString());
                        JSONObject element = serverResp.getJSONArray("candidates").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                        Double lat = element.getDouble("lat");
                        Double lng = element.getDouble("lng");

                        Uri gmmIntentUri = Uri.parse("geo:" + lat +"," + lng);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapIntent);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(StoreItemView.this, R.string.error_directions, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });
        }
    }
