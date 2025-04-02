package com.example.product.service.base;

import com.example.product.api.model.dto.ProductDTO;
import com.example.product.api.model.dto.ProductRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
    import java.util.UUID;

/**
* Base service interface for Product.
* Generated by EasyBase Code Generator.
*/
public interface ProductBaseService {
/**
* Creates a new Product.
*
* @param requestDTO the request DTO
* @return the created DTO
*/
ProductDTO create(ProductRequestDTO requestDTO);

/**
* Updates an existing Product.
*
* @param id the ID
* @param requestDTO the request DTO
* @return the updated DTO
*/
ProductDTO update(java.util.UUID id, ProductRequestDTO requestDTO);

/**
* Retrieves a Product by ID.
*
* @param id the ID
* @return the DTO, if found
*/
ProductDTO findById(java.util.UUID id);

/**
* Retrieves all Product entities with pagination.
*
* @param pageable the pagination information
* @return a page of DTOs
*/
Page<ProductDTO> findAll(Pageable pageable);

    /**
    * Deletes a Product by ID.
    *
    * @param id the ID
    */
    void delete(java.util.UUID id);

    /**
    * Searches for Product entities.
    *
    * @param search the search query
    * @param filter the filter query
    * @param sort the sort expression
    * @param pageable the pagination information
    * @return a page of DTOs
    */
    Page<ProductDTO> search(String search, String filter, String sort, Pageable pageable);
        }