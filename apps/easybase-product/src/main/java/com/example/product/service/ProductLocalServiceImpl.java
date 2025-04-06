package com.example.product.service;

import com.example.product.service.base.ProductBaseLocalServiceImpl;
import com.example.product.repository.ProductJpaRepository;
import com.example.product.api.model.mapper.ProductMapper;
import com.easybase.core.sync.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* Local service implementation for Product.
* This class extends the base service implementation and can be customized.
*/
@Service
@Transactional
public class ProductLocalServiceImpl extends ProductBaseLocalServiceImpl implements ProductLocalService {

@Autowired
public ProductLocalServiceImpl(
ProductJpaRepository repository,
ProductMapper mapper,
SyncService syncService) {
super(repository, mapper, syncService);
}

// CUSTOM CODE START: methods
// Add custom service methods here
// CUSTOM CODE END: methods
}