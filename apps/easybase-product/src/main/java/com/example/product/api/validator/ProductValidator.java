package com.example.product.api.validator;

import com.example.product.api.model.dto.ProductRequestDTO;
import com.example.product.repository.ProductJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
* Validator for Product requests.
* This class validates Product request DTOs beyond simple annotation validations.
*/
@Component
public class ProductValidator implements Validator {

private final ProductJpaRepository repository;

@Autowired
public ProductValidator(ProductJpaRepository repository) {
this.repository = repository;
}

@Override
public boolean supports(Class<?> clazz) {
return ProductRequestDTO.class.isAssignableFrom(clazz);
}

@Override
public void validate(Object target, Errors errors) {
ProductRequestDTO dto = (ProductRequestDTO) target;

// CUSTOM CODE START: validate
// Add custom validation logic here
        // Check if sku is unique
        if (dto.getSku() != null && repository.existsBySku(dto.getSku())) {
        errors.rejectValue("sku", "duplicate", "Sku already exists");
        }
// CUSTOM CODE END: validate
}
}