package ${entity.componentPackage("infrastructure.config")};

import com.easybase.core.collection.EntityDefinition;
import com.easybase.core.collection.FieldDefinition;
import com.easybase.core.collection.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

/**
* Schema configuration for ${entity.name}.
* This class registers the entity with the collection service.
*/
@Configuration
public class ${entity.name}SchemaConfig {

@Autowired
private CollectionService collectionService;

@Bean
public EntityDefinition ${entity.name?uncap_first}Definition() {
EntityDefinition definition = new EntityDefinition("${entity.name}", "${entity.fullTableName}");

// Add fields
<#list entity.fields as field>
    FieldDefinition ${field.name}Field = new FieldDefinition("${field.name}", "${field.type}", ${field.primaryKey?string});
    <#if field.nullable??>
        ${field.name}Field.setNullable(${field.nullable?string});
    </#if>
    <#if field.length?? && field.length gt 0>
        ${field.name}Field.setLength(${field.length?c});
    </#if>
    <#if field.defaultValue??>
        <#if field.defaultValue?is_string>
            ${field.name}Field.setDefaultValue("${field.defaultValue}");
        <#else>
            ${field.name}Field.setDefaultValue(${field.defaultValue?string});
        </#if>
    </#if>
    <#if field.searchMapping??>
        Map<String, Object> ${field.name}SearchMapping = new HashMap<>();
        ${field.name}SearchMapping.put("type", "${field.searchMapping.type}");
        <#if field.searchMapping.analyzer??>
            ${field.name}SearchMapping.put("analyzer", "${field.searchMapping.analyzer}");
        </#if>
        <#if field.searchMapping.fields?size gt 0>
            Map<String, Object> ${field.name}Fields = new HashMap<>();
            <#list field.searchMapping.fields?keys as fieldKey>
                <#if field.searchMapping.fields[fieldKey]?is_boolean>
                    ${field.name}Fields.put("${fieldKey}", ${field.searchMapping.fields[fieldKey]?string});
                <#elseif field.searchMapping.fields[fieldKey]?is_string>
                    ${field.name}Fields.put("${fieldKey}", "${field.searchMapping.fields[fieldKey]}");
                <#else>
                    ${field.name}Fields.put("${fieldKey}", ${field.searchMapping.fields[fieldKey]?c});
                </#if>
            </#list>
            ${field.name}SearchMapping.put("fields", ${field.name}Fields);
        </#if>
        ${field.name}Field.setSearchMapping(${field.name}SearchMapping);
    </#if>
    definition.addField(${field.name}Field);
</#list>

// Register with collection service
collectionService.registerEntityDefinition(definition);

return definition;
}
}