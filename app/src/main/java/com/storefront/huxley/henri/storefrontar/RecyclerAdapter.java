package com.storefront.huxley.henri.storefrontar;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.StoreView>{
    public static class StoreView extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView storeName;
        TextView storeAddress;
        TextView storeDistance;
        ImageView storePicture;

        StoreView(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cv);
            storeName = itemView.findViewById(R.id.store_name);
            storeAddress = itemView.findViewById(R.id.store_address);
            storeDistance = itemView.findViewById(R.id.store_distance);
            storePicture = itemView.findViewById(R.id.store_photo);
        }
    }
    private List<Store> stores;
    RecyclerAdapter(List<Store> stores) {
        this.stores = stores;
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
    @Override
    public void onBindViewHolder(StoreView storeView, int i) {
        storeView.storeName.setText(stores.get(i).name);
        storeView.storeAddress.setText(stores.get(i).address);
        storeView.storeDistance.setText(stores.get(i).distance);
        storeView.storePicture.setImageResource(stores.get(i).photo);
    }
    @Override
    public int getItemCount() {
        return stores.size();
    }
}