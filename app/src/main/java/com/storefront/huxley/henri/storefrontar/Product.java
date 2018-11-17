package com.storefront.huxley.henri.storefrontar;

import java.io.Serializable;

public class Product implements Serializable {
    public String productName;
    public String productDescription;
    public String productObjectName;
    public String productCost;


    public Product(String productName, String ProductDescription,String ProductObjectName, String ProductCost) {
    this.productName = productName;
    this.productDescription = ProductDescription;
    this.productObjectName = ProductObjectName;
    this.productCost = ProductCost;
    }
}
