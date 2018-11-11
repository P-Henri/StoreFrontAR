package com.storefront.huxley.henri.storefrontar;

public class FoundStores {
String StoreName;
String StoreAddress;
Double StoreRating;
Boolean OpenNow;

//When used its returning package name followed by object id?
public String ToString()
{
    return "STORE NAME: " + StoreName + "Address:" + StoreAddress + "Store Rating:" + StoreRating + " Open Now:" + OpenNow;
}
}
