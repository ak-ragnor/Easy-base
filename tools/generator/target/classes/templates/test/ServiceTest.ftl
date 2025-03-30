package ${entity.componentPackage("service")}.test;

import ${entity.componentPackage("model")}.${entity.name};
import ${entity.componentPackage("repository")}.${entity.name}JpaRepository;
import ${entity.componentPackage("service")}.${entity.name}LocalService;
import ${entity.componentPackage("api")}.model.dto.${entity.name}DTO;
import ${entity.componentPackage("api")}.model.dto.${entity.name}RequestDTO;
import ${entity.componentPackage("api")}.model.mapper.${entity.name}Mapper;
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
<#assign primaryKey = entity.primaryKey>
<#if primaryKey?has_content>
    import ${primaryKey.get().javaType};
</#if>

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
* Test class for ${entity.name}LocalService.
*/
public class ${entity.name}ServiceTest {

@Mock
private ${entity.name}JpaRepository repository;

@Mock
private ${entity.name}Mapper mapper;

@Mock
private SyncService syncService;

private ${entity.name}LocalService service;

@BeforeEach
public void setUp() {
MockitoAnnotations.openMocks(this);
service = new ${entity.name}LocalServiceImpl(repository, mapper, syncService);
}

@Test
public void testCreate() {
// Given
${entity.name}RequestDTO requestDTO = new ${entity.name}RequestDTO();
${entity.name} entity = new ${entity.name}();
${entity.name}DTO dto = new ${entity.name}DTO();

<#assign primaryKey = entity.primaryKey>
<#if primaryKey?has_content>
    entity.setId(<#if primaryKey.get().type == "UUID">UUID.randomUUID()<#else>1L</#if>);
    dto.setId(entity.getId());
</#if>

when(mapper.toEntity(requestDTO)).thenReturn(entity);
when(repository.save(entity)).thenReturn(entity);
when(mapper.toDTO(entity)).thenReturn(dto);

// When
${entity.name}DTO result = service.create(requestDTO);

// Then
assertNotNull(result);
<#if primaryKey?has_content>
    assertEquals(entity.getId(), result.getId());
</#if>
verify(repository).save(entity);
verify(syncService).syncRecord(anyString(), any(${entity.name}.class));
}

@Test
public void testUpdate() {
// Given
<#if primaryKey?has_content>
    ${primaryKey.get().javaType} id = <#if primaryKey.get().type == "UUID">UUID.randomUUID()<#else>1L</#if>;
<#else>
    Long id = 1L;
</#if>
${entity.name}RequestDTO requestDTO = new ${entity.name}RequestDTO();
${entity.name} entity = new ${entity.name}();
${entity.name}DTO dto = new ${entity.name}DTO();

<#if primaryKey?has_content>
    entity.setId(id);
    dto.setId(id);
</#if>

when(repository.findById(id)).thenReturn(Optional.of(entity));
when(repository.save(entity)).thenReturn(entity);
when(mapper.toDTO(entity)).thenReturn(dto);

// When
${entity.name}DTO result = service.update(id, requestDTO);

// Then
assertNotNull(result);
<#if primaryKey?has_content>
    assertEquals(id, result.getId());
</#if>
verify(mapper).updateEntity(entity, requestDTO);
verify(repository).save(entity);
verify(syncService).syncRecord(anyString(), any(${entity.name}.class));
}

@Test
public void testFindById() {
// Given
<#if primaryKey?has_content>
    ${primaryKey.get().javaType} id = <#if primaryKey.get().type == "UUID">UUID.randomUUID()<#else>1L</#if>;
<#else>
    Long id = 1L;
</#if>
${entity.name} entity = new ${entity.name}();
${entity.name}DTO dto = new ${entity.name}DTO();

<#if primaryKey?has_content>
    entity.setId(id);
    dto.setId(id);
</#if>

<#if entity.isSoftDeleteEnabled()>
    when(repository.findByIdActive(id)).thenReturn(Optional.of(entity));
<#else>
    when(repository.findById(id)).thenReturn(Optional.of(entity));
</#if>
when(mapper.toDTO(entity)).thenReturn(dto);

// When
Optional<${entity.name}DTO> result = service.findById(id);

    // Then
    assertTrue(result.isPresent());
    <#if primaryKey?has_content>
        assertEquals(id, result.get().getId());
    </#if>
    }

    @Test
    public void testFindAll() {
    // Given
    Pageable pageable = PageRequest.of(0, 10);
    ${entity.name} entity = new ${entity.name}();
    ${entity.name}DTO dto = new ${entity.name}DTO();
    List<${entity.name}> entities = Arrays.asList(entity);
    Page<${entity.name}> page = new PageImpl<>(entities, pageable, entities.size());

    <#if entity.isSoftDeleteEnabled()>
        when(repository.findAllActive()).thenReturn(entities);
    <#else>
        when(repository.findAll(pageable)).thenReturn(page);
    </#if>
    when(mapper.toDTO(entity)).thenReturn(dto);

    // When
    Page<${entity.name}DTO> result = service.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        }

        @Test
        public void testDelete() {
        // Given
        <#if primaryKey?has_content>
            ${primaryKey.get().javaType} id = <#if primaryKey.get().type == "UUID">UUID.randomUUID()<#else>1L</#if>;
        <#else>
            Long id = 1L;
        </#if>

        <#if entity.isSoftDeleteEnabled()>
            ${entity.name} entity = new ${entity.name}();
            <#if primaryKey?has_content>
                entity.setId(id);
            </#if>
            when(repository.findById(id)).thenReturn(Optional.of(entity));
        <#else>
            when(repository.existsById(id)).thenReturn(true);
        </#if>

        // When
        boolean result = service.delete(id);

        // Then
        assertTrue(result);
        <#if entity.isSoftDeleteEnabled()>
            verify(repository).save(entity);
            verify(syncService).syncRecord(anyString(), any(${entity.name}.class));
        <#else>
            verify(repository).deleteById(id);
            verify(syncService).deleteRecord(anyString(), anyString());
        </#if>
        }

        // CUSTOM CODE START: tests
        // Add custom tests here
        // CUSTOM CODE END: tests
        }