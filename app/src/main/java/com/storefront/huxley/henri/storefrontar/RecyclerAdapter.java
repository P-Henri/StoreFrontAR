package com.storefront.huxley.henri.storefrontar;

import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.StoreView> {
    static private List<Store> stores;
    RecyclerAdapter rc = this;
    static int cardViewIndexId = 0;
    public static class StoreView extends RecyclerView.ViewHolder {
        StoreView sv = this;
        Store store;
        CardView cardView;
        TextView storeName;
        TextView storeAddress;
        RatingBar storeRating;
        ImageView storePicture;

        StoreView(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cv);



            storeName = itemView.findViewById(R.id.store_name);
            storeAddress = itemView.findViewById(R.id.store_address);
            storeRating = itemView.findViewById(R.id.store_rating);
            storePicture = itemView.findViewById(R.id.store_photo);

            storeRating.setNumStars(5);
            storeRating.setIsIndicator(true);
        }
    }

    RecyclerAdapter(List<Store> stores) {
        this.stores = stores;

        cardViewIndexId = 0;
    }



    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public StoreView onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_storesnearitem, viewGroup, false);
        return new StoreView(view);
    }

    Vector<Integer> intexAssignedClickEvent = new Vector<>();
    @Override
    public void onBindViewHolder(StoreView storeView, int i) {
        storeView.storeName.setText(stores.get(i).name);
        storeView.storeAddress.setText(stores.get(i).address);
        storeView.storeRating.setRating(stores.get(i).rating);
        storeView.store = stores.get(i);
        storeView.storePicture.setImageResource(R.drawable.icon_two);
        storeView.storePicture.setMaxWidth(50);
        storeView.storePicture.setMaxHeight(50);

        //Confirm the index hasn't already been set a on click event based on index added into global vector and
        //Since I increments if scrolling up even make sure its withi the size of stores.
        if(!intexAssignedClickEvent.contains(i) && i < stores.size()) {
            storeView.cardView.setId(cardViewIndexId);
            storeView.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("ONCLICK", v.getId() + " ");
                    Context c = v.getContext();
                    Intent intent = new Intent(v.getContext(), StoreItemView.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("store_data",storeView.store);
                    intent.putExtras(bundle);
                    c.startActivity(intent);
                }
            });
            ++cardViewIndexId;
        }
        intexAssignedClickEvent.add(i);
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }
}