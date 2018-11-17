package com.storefront.huxley.henri.storefrontar;

import java.io.Serializable;

public class Store implements Serializable {
    public String name;
    public String address;
    public float rating;
    public String photoReference;
    public String reference;
    public String websiteUrl;
    public String phoneno;

    public Store(String name, String address, float rating, String reference, String photoReference) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.reference = reference;
        this.photoReference = photoReference;
    }

    public void SetExtras(String websiteUrl, String phoneno) {
        this.websiteUrl = websiteUrl;
        this.phoneno = phoneno;
    }
}
