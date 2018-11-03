package com.storefront.huxley.henri.storefrontar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onSearchClick(View view) {
        Intent intent = new Intent(this, StoresNearActivity.class);
        startActivity(intent);
    }
}
