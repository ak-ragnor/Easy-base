package ${entity.componentPackage("api")}.test;

import ${entity.componentPackage("api")}.${entity.name}Controller;
import ${entity.componentPackage("service")}.${entity.name}Service;
import ${entity.componentPackage("api")}.model.dto.${entity.name}DTO;
import ${entity.componentPackage("api")}.model.dto.${entity.name}RequestDTO;
import ${entity.componentPackage("api")}.model.dto.${entity.name}ResponseDTO;
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
<#assign primaryKey = entity.primaryKey>
<#if primaryKey?has_content>
    import ${primaryKey.get().javaType};
</#if>

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
* Test class for ${entity.name}Controller.
*/
public class ${entity.name}ControllerTest {

private MockMvc mockMvc;

@Mock
private ${entity.name}Service service;

private ObjectMapper objectMapper;

@BeforeEach
public void setUp() {
MockitoAnnotations.openMocks(this);
${entity.name}Controller controller = new ${entity.name}Controller(service);
mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
objectMapper = new ObjectMapper();
}

@Test
public void testCreate() throws Exception {
// Given
${entity.name}RequestDTO requestDTO = new ${entity.name}RequestDTO();
${entity.name}DTO dto = new ${entity.name}DTO();
<#if primaryKey?has_content>
    dto.setId(<#if primaryKey.get().type == "UUID">UUID.randomUUID()<#else>1L</#if>);
</#if>

when(service.create(any(${entity.name}RequestDTO.class))).thenReturn(dto);

// When/Then
mockMvc.perform(post("/api/${entity.table}")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(requestDTO)))
.andExpect(status().isCreated())
.andExpect(jsonPath("$.id").exists());
}

@Test
public void testUpdate() throws Exception {
// Given
<#if primaryKey?has_content>
    ${primaryKey.get().javaType} id = <#if primaryKey.get().type == "UUID">UUID.randomUUID()<#else>1L</#if>;
<#else>
    Long id = 1L;
</#if>
${entity.name}RequestDTO requestDTO = new ${entity.name}RequestDTO();
${entity.name}DTO dto = new ${entity.name}DTO();
<#if primaryKey?has_content>
    dto.setId(id);
</#if>

when(service.update(eq(id), any(${entity.name}RequestDTO.class))).thenReturn(dto);

// When/Then
mockMvc.perform(put("/api/${entity.table}/" + id)
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(requestDTO)))
.andExpect(status().isOk())
.andExpect(jsonPath("$.id").exists());
}

@Test
public void testFindById() throws Exception {
// Given
<#if primaryKey?has_content>
    ${primaryKey.get().javaType} id = <#if primaryKey.get().type == "UUID">UUID.randomUUID()<#else>1L</#if>;
<#else>
    Long id = 1L;
</#if>
${entity.name}DTO dto = new ${entity.name}DTO();
<#if primaryKey?has_content>
    dto.setId(id);
</#if>

when(service.findById(id)).thenReturn(dto);

// When/Then
mockMvc.perform(get("/api/${entity.table}/" + id))
.andExpect(status().isOk())
.andExpect(jsonPath("$.id").exists());
}

@Test
public void testFindAll() throws Exception {
// Given
${entity.name}DTO dto = new ${entity.name}DTO();
<#if primaryKey?has_content>
    dto.setId(<#if primaryKey.get().type == "UUID">UUID.randomUUID()<#else>1L</#if>);
</#if>

when(service.findAll(any(Pageable.class)))
.thenReturn(new PageImpl<>(Arrays.asList(dto)));

// When/Then
mockMvc.perform(get("/api/${entity.table}"))
.andExpect(status().isOk())
.andExpect(jsonPath("$.content").isArray())
.andExpect(jsonPath("$.content[0].id").exists());
}

@Test
public void testDelete() throws Exception {
// Given
<#if primaryKey?has_content>
    ${primaryKey.get().javaType} id = <#if primaryKey.get().type == "UUID">UUID.randomUUID()<#else>1L</#if>;
<#else>
    Long id = 1L;
</#if>

doNothing().when(service).delete(id);

// When/Then
mockMvc.perform(delete("/api/${entity.table}/" + id))
.andExpect(status().isNoContent());
}

@Test
public void testSearch() throws Exception {
// Given
${entity.name}DTO dto = new ${entity.name}DTO();
<#if primaryKey?has_content>
    dto.setId(<#if primaryKey.get().type == "UUID">UUID.randomUUID()<#else>1L</#if>);
</#if>

when(service.search(anyString(), anyString(), anyString(), any(Pageable.class)))
.thenReturn(new PageImpl<>(Arrays.asList(dto)));

// When/Then
mockMvc.perform(get("/api/${entity.table}/search")
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