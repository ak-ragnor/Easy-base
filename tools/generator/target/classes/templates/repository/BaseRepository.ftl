package ${entity.baseComponentPackage("repository")};

import ${entity.componentPackage("model")}.${entity.name};
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;

/**
* Base repository interface for ${entity.name}.
* Generated by EasyBase Code Generator.
*/
public interface ${entity.name}BaseJpaRepository extends JpaRepository<${entity.name}, <#assign primaryKey = entity.primaryKey><#if primaryKey?has_content>${primaryKey.get().javaType}<#else>Long</#if>> {
<#if entity.isSoftDeleteEnabled()>
    @Query("SELECT e FROM ${entity.name} e WHERE e.${entity.softDeleteField} = false")
    List<${entity.name}> findAllActive();

    @Query("SELECT e FROM ${entity.name} e WHERE e.id = :id AND e.${entity.softDeleteField} = false")
    Optional<${entity.name}> findByIdActive(@Param("id") <#if primaryKey?has_content>${primaryKey.get().javaType}<#else>Long</#if> id);
</#if>

<#-- Add existsBy methods for unique fields -->
<#list entity.fields as field>
    <#if field.unique && !field.primaryKey>
        boolean existsBy${field.name?cap_first}(${field.javaType} ${field.name});
    </#if>
</#list>

<#-- Add finder methods with parameter validation -->
<#list entity.finders as finder>
<#-- Check if all parameters correspond to actual entity fields or follow naming conventions -->
    <#assign validParams = true>
    <#list finder.parameters as param>
        <#assign fieldExists = false>
    <#-- Check for direct field match -->
        <#list entity.fields as field>
            <#if field.name == param.name>
                <#assign fieldExists = true>
            </#if>
        </#list>
    <#-- Check for ID field (foreign key) -->
        <#if param.name?ends_with("Id")>
            <#assign fieldName = param.name?remove_ending("Id")>
            <#list entity.fields as field>
                <#if field.name == fieldName || field.name == fieldName + "Id">
                    <#assign fieldExists = true>
                </#if>
            </#list>
        </#if>
    <#-- If parameter doesn't match any field, it's invalid -->
        <#if !fieldExists>
            <#assign validParams = false>
        </#if>
    </#list>

<#-- Only generate finder method if all parameters are valid -->
    <#if validParams>
    <#-- Get method signature string correctly -->
        <#assign methodSig = finder.getMethodSignature()>
        ${methodSig};
    <#else>
        // CUSTOM FINDER: ${finder.name}
        // Note: This finder has parameters that don't match entity fields
        // You need to implement this manually with a custom query
        // ${finder.getMethodSignature()};
    </#if>
</#list>
}