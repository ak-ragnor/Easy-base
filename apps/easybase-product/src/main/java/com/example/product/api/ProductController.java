package com.example.product.api;

import com.example.product.api.base.ProductBaseController;
import com.example.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* Controller for Product.
* This class extends the base controller and can be customized.
*/
@RestController
@RequestMapping("/api/eb_product")
public class ProductController extends ProductBaseController {

@Autowired
public ProductController(ProductService service) {
super(service);
}

// CUSTOM CODE START: methods
// Add custom controller methods here
// CUSTOM CODE END: methods
}