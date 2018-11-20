package com.storefront.huxley.henri.storefrontar;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class StoreItemView extends AppCompatActivity {
    private ImageView image, image2;
    private TextView name, address, website, phone, price, description;
    private RatingBar rating;
    private Random rnd = new Random();
    private static final double MIN_OPENGL_VERSION = 3.0;
    Product product;
    Store store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_item_view);
        Store store = (Store) getIntent().getExtras().getSerializable("store_data");
        this.store = store;

        name = findViewById(R.id.storeitem_name);
        address = findViewById(R.id.storeitem_address);
        website = findViewById(R.id.storeitem_website);
        phone = findViewById(R.id.storeitem_phone);
        price = findViewById(R.id.storeitem_price);
        rating = findViewById(R.id.storeitem_rating);
        image2 = findViewById(R.id.imageView2);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        image2.setMinimumWidth(displayMetrics.widthPixels);

        name.setText(store.name);
        hideIfNull(address, store.address);
        if(store.websiteUrl != null) {
            website.setText("click here to view website");
        }
        else {
            hideIfNull(website, store.websiteUrl);
        }
        hideIfNull(phone, store.phoneno);

        rating.setNumStars(5);
        rating.setIsIndicator(true);
        rating.setRating(store.rating);

        ((TextView) findViewById(R.id.storeitem_description)).setText(store.selectedProduct.productDescription);
        ((TextView) findViewById(R.id.storeitem_price)).setText(store.selectedProduct.productCost);

        try {
            Resources resources = this.getResources();
            final int resourceId = resources.getIdentifier(store.selectedProduct.productObjectName, "drawable", this.getPackageName());
            ((ImageView) findViewById(R.id.productImg)).setImageDrawable(resources.getDrawable(resourceId));
        }
        catch(Exception e){
            ((ImageView) findViewById(R.id.productImg)).setVisibility(View.INVISIBLE);
        }
    }

    public void hideIfNull(TextView txt, String value) {
        if (value == null)
            txt.setVisibility(View.INVISIBLE);
        else
            txt.setText(value);
    }

    public void viewInAR(View view) {
        Intent intent = new Intent(this, ARCore.class);
        intent.putExtra("filename", store.selectedProduct.productObjectName + ".sfb");
        startActivity(intent);
    }


    public void openPhone(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + store.phoneno));
        startActivity(intent);
    }

    public void openWebsite(View view) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(store.websiteUrl));
        startActivity(i);
    }

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
            HttpUtils.getByUrl(getCordsURL, rp, new JsonHttpResponseHandler() {
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
                        Toast.makeText(StoreItemView.this, "Error Obtaining Directions", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });
        }
    }
