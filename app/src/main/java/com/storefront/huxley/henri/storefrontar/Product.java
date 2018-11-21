package com.storefront.huxley.henri.storefrontar;

import java.io.Serializable;



/*
Developer: Evan Yohnicki-Huxley
Purpose: Serializable to pass between activities
Date: November 16th 2018
*/

public class Product implements Serializable {

    //All Product Values and a constructor to set the values
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
