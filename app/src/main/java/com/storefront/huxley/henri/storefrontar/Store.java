package com.storefront.huxley.henri.storefrontar;

import java.io.Serializable;


/*
Developer: Evan Yohnicki-Huxley & Patrick Henri
Purpose: Store object, serializable to pass between activities
Date: November 16th 2018
*/
public class Store implements Serializable {
    //Members and Constructor
    public String name;
    public String address;
    public float rating;
    public String photoReference;
    public String reference;
    public String websiteUrl;
    public String phoneno;
    public Product selectedProduct;

    public Store(String name, String address, float rating, String reference, String photoReference, Product selectedProduct) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.reference = reference;
        this.photoReference = photoReference;
        this.selectedProduct = selectedProduct;
    }

    //Sets values later on since these values aren't supplied from the same places website api call.
    public void SetExtras(String websiteUrl, String phoneno) {
        this.websiteUrl = websiteUrl;
        this.phoneno = phoneno;
    }
}
