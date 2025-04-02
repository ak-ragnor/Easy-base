package com.example.product.service.test;

import com.example.product.model.Product;
import com.example.product.repository.ProductJpaRepository;
import com.example.product.service.ProductLocalService;
import com.example.product.api.model.dto.ProductDTO;
import com.example.product.api.model.dto.ProductRequestDTO;
import com.example.product.api.model.mapper.ProductMapper;
import com.easybase.core.sync.SyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
    import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
* Test class for ProductLocalService.
*/
public class ProductServiceTest {

@Mock
private ProductJpaRepository repository;

@Mock
private ProductMapper mapper;

@Mock
private SyncService syncService;

private ProductLocalService service;

@BeforeEach
public void setUp() {
MockitoAnnotations.openMocks(this);
service = new ProductLocalServiceImpl(repository, mapper, syncService);
}

@Test
public void testCreate() {
// Given
ProductRequestDTO requestDTO = new ProductRequestDTO();
Product entity = new Product();
ProductDTO dto = new ProductDTO();

    entity.setId(UUID.randomUUID());
    dto.setId(entity.getId());

when(mapper.toEntity(requestDTO)).thenReturn(entity);
when(repository.save(entity)).thenReturn(entity);
when(mapper.toDTO(entity)).thenReturn(dto);

// When
ProductDTO result = service.create(requestDTO);

// Then
assertNotNull(result);
    assertEquals(entity.getId(), result.getId());
verify(repository).save(entity);
verify(syncService).syncRecord(anyString(), any(Product.class));
}

@Test
public void testUpdate() {
// Given
    java.util.UUID id = UUID.randomUUID();
ProductRequestDTO requestDTO = new ProductRequestDTO();
Product entity = new Product();
ProductDTO dto = new ProductDTO();

    entity.setId(id);
    dto.setId(id);

when(repository.findById(id)).thenReturn(Optional.of(entity));
when(repository.save(entity)).thenReturn(entity);
when(mapper.toDTO(entity)).thenReturn(dto);

// When
ProductDTO result = service.update(id, requestDTO);

// Then
assertNotNull(result);
    assertEquals(id, result.getId());
verify(mapper).updateEntity(entity, requestDTO);
verify(repository).save(entity);
verify(syncService).syncRecord(anyString(), any(Product.class));
}

@Test
public void testFindById() {
// Given
    java.util.UUID id = UUID.randomUUID();
Product entity = new Product();
ProductDTO dto = new ProductDTO();

    entity.setId(id);
    dto.setId(id);

    when(repository.findByIdActive(id)).thenReturn(Optional.of(entity));
when(mapper.toDTO(entity)).thenReturn(dto);

// When
Optional<ProductDTO> result = service.findById(id);

    // Then
    assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    public void testFindAll() {
    // Given
    Pageable pageable = PageRequest.of(0, 10);
    Product entity = new Product();
    ProductDTO dto = new ProductDTO();
    List<Product> entities = Arrays.asList(entity);
    Page<Product> page = new PageImpl<>(entities, pageable, entities.size());

        when(repository.findAllActive()).thenReturn(entities);
    when(mapper.toDTO(entity)).thenReturn(dto);

    // When
    Page<ProductDTO> result = service.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        }

        @Test
        public void testDelete() {
        // Given
            java.util.UUID id = UUID.randomUUID();

            Product entity = new Product();
                entity.setId(id);
            when(repository.findById(id)).thenReturn(Optional.of(entity));

        // When
        boolean result = service.delete(id);

        // Then
        assertTrue(result);
            verify(repository).save(entity);
            verify(syncService).syncRecord(anyString(), any(Product.class));
        }

        // CUSTOM CODE START: tests
        // Add custom tests here
        // CUSTOM CODE END: tests
        }