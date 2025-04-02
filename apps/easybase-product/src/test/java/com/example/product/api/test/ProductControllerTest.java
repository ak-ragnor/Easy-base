package com.example.product.api.test;

import com.example.product.api.ProductController;
import com.example.product.service.ProductService;
import com.example.product.api.model.dto.ProductDTO;
import com.example.product.api.model.dto.ProductRequestDTO;
import com.example.product.api.model.dto.ProductResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
    import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
* Test class for ProductController.
*/
public class ProductControllerTest {

private MockMvc mockMvc;

@Mock
private ProductService service;

private ObjectMapper objectMapper;

@BeforeEach
public void setUp() {
MockitoAnnotations.openMocks(this);
ProductController controller = new ProductController(service);
mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
objectMapper = new ObjectMapper();
}

@Test
public void testCreate() throws Exception {
// Given
ProductRequestDTO requestDTO = new ProductRequestDTO();
ProductDTO dto = new ProductDTO();
    dto.setId(UUID.randomUUID());

when(service.create(any(ProductRequestDTO.class))).thenReturn(dto);

// When/Then
mockMvc.perform(post("/api/eb_product")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(requestDTO)))
.andExpect(status().isCreated())
.andExpect(jsonPath("$.id").exists());
}

@Test
public void testUpdate() throws Exception {
// Given
    java.util.UUID id = UUID.randomUUID();
ProductRequestDTO requestDTO = new ProductRequestDTO();
ProductDTO dto = new ProductDTO();
    dto.setId(id);

when(service.update(eq(id), any(ProductRequestDTO.class))).thenReturn(dto);

// When/Then
mockMvc.perform(put("/api/eb_product/" + id)
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(requestDTO)))
.andExpect(status().isOk())
.andExpect(jsonPath("$.id").exists());
}

@Test
public void testFindById() throws Exception {
// Given
    java.util.UUID id = UUID.randomUUID();
ProductDTO dto = new ProductDTO();
    dto.setId(id);

when(service.findById(id)).thenReturn(dto);

// When/Then
mockMvc.perform(get("/api/eb_product/" + id))
.andExpect(status().isOk())
.andExpect(jsonPath("$.id").exists());
}

@Test
public void testFindAll() throws Exception {
// Given
ProductDTO dto = new ProductDTO();
    dto.setId(UUID.randomUUID());

when(service.findAll(any(Pageable.class)))
.thenReturn(new PageImpl<>(Arrays.asList(dto)));

// When/Then
mockMvc.perform(get("/api/eb_product"))
.andExpect(status().isOk())
.andExpect(jsonPath("$.content").isArray())
.andExpect(jsonPath("$.content[0].id").exists());
}

@Test
public void testDelete() throws Exception {
// Given
    java.util.UUID id = UUID.randomUUID();

doNothing().when(service).delete(id);

// When/Then
mockMvc.perform(delete("/api/eb_product/" + id))
.andExpect(status().isNoContent());
}

@Test
public void testSearch() throws Exception {
// Given
ProductDTO dto = new ProductDTO();
    dto.setId(UUID.randomUUID());

when(service.search(anyString(), anyString(), anyString(), any(Pageable.class)))
.thenReturn(new PageImpl<>(Arrays.asList(dto)));

// When/Then
mockMvc.perform(get("/api/eb_product/search")
.param("search", "test")
.param("filter", "field eq 'value'")
.param("sort", "field asc"))
.andExpect(status().isOk())
.andExpect(jsonPath("$.content").isArray())
.andExpect(jsonPath("$.content[0].id").exists());
}

// CUSTOM CODE START: tests
// Add custom tests here
// CUSTOM CODE END: tests
}