package com.example.product.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import com.example.product.model.base.ProductBase;

/**
* Entity class for Product.
* This class extends the base class and can be customized.
*/
@Entity
@Table(name = "eb_product")
public class Product extends ProductBase {
    // CUSTOM CODE START: fields
    // Add custom fields here
    // CUSTOM CODE END: fields

    // CUSTOM CODE START: methods
    // Add custom methods here
    // CUSTOM CODE END: methods
}