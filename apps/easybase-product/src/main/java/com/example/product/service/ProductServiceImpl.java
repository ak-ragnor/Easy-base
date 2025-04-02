package com.example.product.service;

import com.example.product.service.base.ProductBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* Service implementation for Product.
* This class extends the base service implementation and can be customized.
*/
@Service
@Transactional
public class ProductServiceImpl extends ProductBaseServiceImpl implements ProductService {

@Autowired
public ProductServiceImpl(ProductLocalService localService) {
super(localService);
}

// CUSTOM CODE START: methods
// Add custom service methods here
// CUSTOM CODE END: methods
}