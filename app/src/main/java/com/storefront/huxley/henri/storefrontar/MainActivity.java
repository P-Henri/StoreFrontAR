package com.storefront.huxley.henri.storefrontar;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements LocationListener {

    double lat, lng;
    private LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /*
    Developer: Evan Yohnicki-Huxley
    Purpose: Obtain User Coordinates using Place API. Once Found move to finding the lists using the location
    Date: November 10th 2018
    */
    public void onSearchClick(View view) {

        String getCordsURL = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=" + ((EditText) findViewById(R.id.address_input)).getText().toString() + "&inputtype=textquery&fields=geometry&key=" + getResources().getString(R.string.placesKey);
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

                Intent intent = new Intent(MainActivity.this, StoresNearListActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                startActivity(intent);

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

    public void onUserCoords(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        ((TextView) findViewById(R.id.txt_Searching)).setVisibility(View.VISIBLE);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates("gps", 0, 0, this);

    }

    @Override
    public void onLocationChanged(Location location) {

        locationManager.removeUpdates(this);
        Intent intent = new Intent(MainActivity.this, StoresNearListActivity.class);
        intent.putExtra("lat",location.getLatitude());
        intent.putExtra("lng",location.getLongitude());
        startActivity(intent);


    }





    /* REQUIRED LISTENERS */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }

}
