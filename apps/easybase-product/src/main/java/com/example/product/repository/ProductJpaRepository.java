package com.example.product.repository;

import com.example.product.repository.base.ProductBaseJpaRepository;
import org.springframework.stereotype.Repository;

/**
* Repository interface for Product.
* This interface extends the base repository and can be customized.
*/
@Repository
public interface ProductJpaRepository extends ProductBaseJpaRepository {
// CUSTOM CODE START: methods
// Add custom repository methods here
// CUSTOM CODE END: methods
}