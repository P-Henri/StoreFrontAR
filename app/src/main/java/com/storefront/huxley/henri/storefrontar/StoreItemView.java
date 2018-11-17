package com.storefront.huxley.henri.storefrontar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.Random;

public class StoreItemView extends AppCompatActivity {
    private ImageView image;
    private TextView name, address, website, phone, price, description;
    private RatingBar rating;
    private Random rnd = new Random();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_item_view);
        Store store = (Store)getIntent().getExtras().getSerializable("store_data");

        name = findViewById(R.id.storeitem_name);
        address = findViewById(R.id.storeitem_address);
        website = findViewById(R.id.storeitem_website);
        phone = findViewById(R.id.storeitem_phone);
        price = findViewById(R.id.storeitem_price);
        rating = findViewById(R.id.storeitem_rating);


        name.setText(store.name);
        address.setText(store.address);
        website.setText(store.websiteUrl);
        phone.setText(store.phoneno);

        rating.setMax(5);
        rating.setNumStars(5);
        rating.setRating(store.rating);
        rating.setIsIndicator(true);

        double d = rnd.nextDouble()*(1000-12);
        String priceStr = "price: $" + String.format("%.2f", d);
        price.setText(priceStr);

    }
}
