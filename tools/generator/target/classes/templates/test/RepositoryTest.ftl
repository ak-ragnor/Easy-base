package ${entity.componentPackage("repository")}.test;

import ${entity.componentPackage("model")}.${entity.name};
import ${entity.componentPackage("repository")}.${entity.name}JpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
<#assign primaryKey = entity.primaryKey>
<#if primaryKey?has_content>
    import ${primaryKey.get().javaType};
</#if>

import static org.junit.jupiter.api.Assertions.*;

/**
* Test class for ${entity.name}JpaRepository.
*/
@DataJpaTest
@ActiveProfiles("test")
public class ${entity.name}RepositoryTest {

@Autowired
private ${entity.name}JpaRepository repository;

@Test
public void testSaveAndFind() {
// Given
${entity.name} entity = new ${entity.name}();
<#list entity.fields as field>
    <#if !field.primaryKey && !field.name?starts_with("_")>
        entity.set${field.name?cap_first}(<#if field.type == "String">"test${field.name?cap_first}"<#elseif field.type == "Integer" || field.type == "Long">1<#elseif field.type == "Double" || field.type == "Float">1.0<#elseif field.type == "Boolean">true<#elseif field.type == "Enum">${field.type}.values()[0]<#else>null</#if>);
    </#if>
</#list>

// When
${entity.name} saved = repository.save(entity);

// Then
assertNotNull(saved.getId());

// When
Optional<${entity.name}> found = repository.findById(saved.getId());

// Then
assertTrue(found.isPresent());
assertEquals(saved.getId(), found.get().getId());
<#list entity.fields as field>
    <#if !field.primaryKey && !field.name?starts_with("_")>
        assertEquals(saved.get${field.name?cap_first}(), found.get().get${field.name?cap_first}());
    </#if>
</#list>
}

<#list entity.finders as finder>
    @Test
    public void test${finder.name?cap_first}() {
    // Given
    // Create test data

    // When
    // Call finder method

    // Then
    // Verify results

    // CUSTOM CODE START: ${finder.name}Test
    // Implement test for ${finder.name}
    // CUSTOM CODE END: ${finder.name}Test
    }
</#list>

// CUSTOM CODE START: tests
// Add custom tests here
// CUSTOM CODE END: tests
}