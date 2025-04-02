package com.example.product.repository.test;

import com.example.product.model.Product;
import com.example.product.repository.ProductJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
    import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
* Test class for ProductJpaRepository.
*/
@DataJpaTest
@ActiveProfiles("test")
public class ProductRepositoryTest {

@Autowired
private ProductJpaRepository repository;

@Test
public void testSaveAndFind() {
// Given
Product entity = new Product();
        entity.setName("testName");
        entity.setDescription("testDescription");
        entity.setPrice(1.0);
        entity.setSku("testSku");
        entity.setStatus(Enum.values()[0]);

// When
Product saved = repository.save(entity);

// Then
assertNotNull(saved.getId());

// When
Optional<Product> found = repository.findById(saved.getId());

// Then
assertTrue(found.isPresent());
assertEquals(saved.getId(), found.get().getId());
        assertEquals(saved.getName(), found.get().getName());
        assertEquals(saved.getDescription(), found.get().getDescription());
        assertEquals(saved.getPrice(), found.get().getPrice());
        assertEquals(saved.getSku(), found.get().getSku());
        assertEquals(saved.getStatus(), found.get().getStatus());
}

    @Test
    public void testFindBySku() {
    // Given
    // Create test data

    // When
    // Call finder method

    // Then
    // Verify results

    // CUSTOM CODE START: findBySkuTest
    // Implement test for findBySku
    // CUSTOM CODE END: findBySkuTest
    }
    @Test
    public void testFindByNameContaining() {
    // Given
    // Create test data

    // When
    // Call finder method

    // Then
    // Verify results

    // CUSTOM CODE START: findByNameContainingTest
    // Implement test for findByNameContaining
    // CUSTOM CODE END: findByNameContainingTest
    }
    @Test
    public void testFindByPriceRange() {
    // Given
    // Create test data

    // When
    // Call finder method

    // Then
    // Verify results

    // CUSTOM CODE START: findByPriceRangeTest
    // Implement test for findByPriceRange
    // CUSTOM CODE END: findByPriceRangeTest
    }

// CUSTOM CODE START: tests
// Add custom tests here
// CUSTOM CODE END: tests
}