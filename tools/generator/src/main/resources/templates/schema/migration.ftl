-- Migration script for ${entity.name}
-- Generated by EasyBase Code Generator

CREATE TABLE ${entity.fullTableName} (
<#list entity.fields as field>
    ${field.name} ${field.columnDefinition}<#if field_has_next || entity.auditConfig.enabled || entity.isSoftDeleteEnabled()>,</#if>
</#list>
<#if entity.auditConfig.enabled>

    -- Audit fields
    created_date TIMESTAMP<#if entity.isSoftDeleteEnabled() || entity.auditConfig.hasField("modifiedDate") || entity.auditConfig.hasField("createdBy") || entity.auditConfig.hasField("modifiedBy") || entity.auditConfig.hasField("version")>,</#if>
    <#if entity.auditConfig.hasField("modifiedDate")>
        last_modified_date TIMESTAMP<#if entity.isSoftDeleteEnabled() || entity.auditConfig.hasField("createdBy") || entity.auditConfig.hasField("modifiedBy") || entity.auditConfig.hasField("version")>,</#if>
    </#if>
    <#if entity.auditConfig.hasField("createdBy")>
        created_by VARCHAR(255)<#if entity.isSoftDeleteEnabled() || entity.auditConfig.hasField("modifiedBy") || entity.auditConfig.hasField("version")>,</#if>
    </#if>
    <#if entity.auditConfig.hasField("modifiedBy")>
        last_modified_by VARCHAR(255)<#if entity.isSoftDeleteEnabled() || entity.auditConfig.hasField("version")>,</#if>
    </#if>
    <#if entity.auditConfig.hasField("version")>
        version BIGINT DEFAULT 0<#if entity.isSoftDeleteEnabled()>,</#if>
    </#if>
</#if>
<#if entity.isSoftDeleteEnabled()>

    -- Soft delete field
    ${entity.softDeleteField} BOOLEAN DEFAULT FALSE
</#if>
);

-- Create indexes
<#-- Track created indices to avoid duplicates -->
<#assign indexedFields = []>

<#-- Create unique indices first -->
<#list entity.fields as field>
    <#if field.unique && !field.primaryKey>
        CREATE UNIQUE INDEX idx_${entity.table}_${field.name} ON ${entity.fullTableName}(${field.name});
        <#assign indexedFields = indexedFields + [field.name]>
    </#if>
</#list>

<#-- Create indices for finder method parameters that match actual entity fields -->
<#list entity.finders as finder>
    <#list finder.parameters as param>
    <#-- For foreign key references -->
        <#if param.name?ends_with("Id")>
            <#assign fieldName = param.name?remove_ending("Id")>
        <#-- Check if the foreign key field actually exists -->
            <#assign fieldExists = false>
            <#list entity.fields as field>
                <#if field.name == fieldName + "Id" || (field.name == fieldName && field.type == "Relationship")>
                    <#assign fieldExists = true>
                </#if>
            </#list>
            <#if fieldExists && !(indexedFields?seq_contains(fieldName + "_id"))>
                CREATE INDEX idx_${entity.table}_${fieldName}_id ON ${entity.fullTableName}(${fieldName}_id);
                <#assign indexedFields = indexedFields + [fieldName + "_id"]>
            </#if>
        <#-- For regular fields -->
        <#else>
        <#-- Check if this parameter corresponds to an actual entity field -->
            <#assign fieldExists = false>
            <#list entity.fields as field>
                <#if field.name == param.name>
                    <#assign fieldExists = true>
                </#if>
            </#list>
            <#if fieldExists && !(indexedFields?seq_contains(param.name))>
                CREATE INDEX idx_${entity.table}_${param.name} ON ${entity.fullTableName}(${param.name});
                <#assign indexedFields = indexedFields + [param.name]>
            </#if>
        </#if>
    </#list>
</#list>

<#-- Create composite indices for multi-parameter finder methods if all fields exist -->
<#list entity.finders as finder>
    <#if finder.parameters?size gt 1>
        <#assign validFieldList = []>
        <#list finder.parameters as param>
            <#assign fieldExists = false>
            <#list entity.fields as field>
                <#if field.name == param.name>
                    <#assign fieldExists = true>
                    <#assign validFieldList = validFieldList + [param.name]>
                </#if>
            </#list>
        </#list>
        <#if validFieldList?size gt 1>
            <#assign compositeIndexName = validFieldList?join("_")>
            <#if !(indexedFields?seq_contains(compositeIndexName))>
                CREATE INDEX idx_${entity.table}_${compositeIndexName} ON ${entity.fullTableName}(${validFieldList?join(", ")});
                <#assign indexedFields = indexedFields + [compositeIndexName]>
            </#if>
        </#if>
    </#if>
</#list>