package com.storefront.huxley.henri.storefrontar;

import android.Manifest;
import android.support.annotation.NonNull;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/*
Developer: Evan Yohnicki-Huxley & Patrick Henri
Purpose: Starting Activity Requesting Address or Location Permission
Date: November 10th 2018
*/

public class MainActivity extends AppCompatActivity implements LocationListener {

    double lat, lng;
    private LocationManager locationManager;
    private Button buttonSearch;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSearch = findViewById(R.id.button_search);
        image = findViewById(R.id.img_gps_find);
    }

    /*
    Developer: Evan Yohnicki-Huxley
    Purpose: Obtain User Coordinates using Place API. Once Found move to finding the lists using the location
    Date: November 10th 2018
    */
    public void onSearchClick(View view) {
        //Disable search button after being selected to enforce no spam
        buttonSearch.setClickable(false);
        //generate url for pulling coordinates of location
        String getCordsURL = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=" + ((EditText) findViewById(R.id.address_input)).getText().toString() + "&inputtype=textquery&fields=geometry&key=" + getResources().getString(R.string.placesKey);

        //Generate the Request and Return the JSON
        RequestParams rp = new RequestParams();
        HttpClient.obtainFromUrl(getCordsURL, rp, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("MainActivity_onSuccess", "------ JSON Object : " + response);
                try {
                    //Pull the repsonse into and object
                    JSONObject serverResp = new JSONObject(response.toString());
                    //Narrow down to the coords
                    JSONObject element = serverResp.getJSONArray("candidates").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                    //pull each coord
                    lat = element.getDouble("lat");
                    lng = element.getDouble("lng");

                    //Create intent to obtain list based on coord
                    Intent intent = new Intent(MainActivity.this, StoresNearListActivity.class);
                    intent.putExtra("lat", lat);
                    intent.putExtra("lng", lng);
                    startActivity(intent);

                } catch (JSONException e) {
                    //If there was a error pulling the lat and lng inform the user and re-enable the button
                    Toast.makeText(MainActivity.this, R.string.error_invalid, Toast.LENGTH_LONG).show();
                    buttonSearch.setClickable(true);
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(MainActivity.this, R.string.error_invalid, Toast.LENGTH_LONG).show();
                buttonSearch.setClickable(true);
            }
        });
    }

    /*
    Developer: Evan Yohnicki-Huxley
    Purpose: Obtain User Coordinates using the built in GPS
    Date: November 16th 2018
    */
    public void onUserCoords(View view) {
        image.setClickable(false);
        //If no permissions then request permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        } else {
            //Inform the user its pulling coords and start the update request
            Toast.makeText(this, R.string.pulling_coords, Toast.LENGTH_SHORT).show();
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location mobileLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (mobileLocation != null) {
                Log.e("found with gps", "executeFindCoords: found");
                onLocationChanged(mobileLocation);
            } else {
                //Toast.makeText(this, R.string.network_provider_msg, Toast.LENGTH_LONG).show();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, this);
            }
        }
    }

    /*
    Developer: Evan Yohnicki-Huxley
    Purpose: Obtain User Coordinates using onLocationOverride in the locationManager
    Date: November 16th 2018
    */
    @Override
    public void onLocationChanged(Location location) {
        locationManager.removeUpdates(this);
        Intent intent = new Intent(MainActivity.this, StoresNearListActivity.class);
        intent.putExtra("lat", location.getLatitude());
        intent.putExtra("lng", location.getLongitude());
        startActivity(intent);
    }

    /*
    Developer: Evan Yohnicki-Huxley
    Purpose: Rerun the onUserCoords if they accept the permission for GPS usage
    Date: November 16th 2018
    */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean found = false;
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; ++i) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED && permissions[i].equals("android.permission.ACCESS_COARSE_LOCATION")) {
                Toast.makeText(this, R.string.pulling_coords, Toast.LENGTH_SHORT).show();
                found = true;
                executeFindCoords();
            }
        }
        if (!found) {
            Toast.makeText(this, "Error: You must accept location permission to use this feature!", Toast.LENGTH_LONG).show();
            image.setClickable(true);
        }
    }

    /*
      Developer: Patrick Henri
      Purpose: Executes after permissions are granted if granted.
      Date: November 21th 2018
     */
    public void executeFindCoords() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Inform the user its pulling coords and start the update request
            Toast.makeText(this, R.string.pulling_coords, Toast.LENGTH_SHORT).show();
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location mobileLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (mobileLocation != null) {
                Log.e("found with gps", "executeFindCoords: found");
                onLocationChanged(mobileLocation);
            } else {
                //Toast.makeText(this, R.string.network_provider_msg, Toast.LENGTH_LONG).show();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, this);
            }
        }
    }

    /*
      Developer: Evan Yohnicki-Huxley
      Purpose: Disables the coords searching text if they used that method prior and return to main menu
      Date: November 16th 2018
     */
    @Override
    protected void onResume() {
        super.onResume();
        image.setClickable(true);
        buttonSearch.setClickable(true);
    }

    /* REQUIRED LISTENERS */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
