package ${entity.baseComponentPackage("model")};

import jakarta.persistence.*;
import java.util.Objects;
<#list entity.requiredImports as import>
    import ${import};
</#list>

/**
* Base entity class for ${entity.name}.
* Generated by EasyBase Code Generator.
*/
@MappedSuperclass
public class ${entity.name}Base {
<#list entity.fields as field>
    <#list field.jpaAnnotations as annotation>
        ${annotation}
    </#list>
    private ${field.javaType} ${field.name}<#if field.defaultValue??> = ${field.defaultValue}</#if>;
</#list>

<#if entity.auditConfig.enabled>
    // Audit fields
    @Column(name = "created_date")
    private java.time.Instant createdDate;

    @Column(name = "last_modified_date")
    private java.time.Instant lastModifiedDate;

    <#if entity.auditConfig.hasField("createdBy")>
        @Column(name = "created_by")
        private String createdBy;
    </#if>

    <#if entity.auditConfig.hasField("lastModifiedBy")>
        @Column(name = "last_modified_by")
        private String lastModifiedBy;
    </#if>

    <#if entity.auditConfig.hasField("version")>
        @Version
        @Column(name = "version")
        private Long version;
    </#if>
</#if>

<#if entity.isSoftDeleteEnabled()>
    // Soft delete field
    @Column(name = "${entity.softDeleteField}")
    private boolean ${entity.softDeleteField} = false;
</#if>

// Getters and setters
<#list entity.fields as field>
    public ${field.javaType} get${field.name?cap_first}() {
    return ${field.name};
    }

    public void set${field.name?cap_first}(${field.javaType} ${field.name}) {
    this.${field.name} = ${field.name};
    }
</#list>

<#if entity.auditConfig.enabled>
    // Audit getters and setters
    public java.time.Instant getCreatedDate() {
    return createdDate;
    }

    public void setCreatedDate(java.time.Instant createdDate) {
    this.createdDate = createdDate;
    }

    public java.time.Instant getLastModifiedDate() {
    return lastModifiedDate;
    }

    public void setLastModifiedDate(java.time.Instant lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
    }

    <#if entity.auditConfig.hasField("createdBy")>
        public String getCreatedBy() {
        return createdBy;
        }

        public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        }
    </#if>

    <#if entity.auditConfig.hasField("lastModifiedBy")>
        public String getLastModifiedBy() {
        return lastModifiedBy;
        }

        public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
        }
    </#if>

    <#if entity.auditConfig.hasField("version")>
        public Long getVersion() {
        return version;
        }

        public void setVersion(Long version) {
        this.version = version;
        }
    </#if>
</#if>

<#if entity.isSoftDeleteEnabled()>
    // Soft delete getters and setters
    public boolean is${entity.softDeleteField?cap_first}() {
    return ${entity.softDeleteField};
    }

    public void set${entity.softDeleteField?cap_first}(boolean ${entity.softDeleteField}) {
    this.${entity.softDeleteField} = ${entity.softDeleteField};
    }
</#if>

@Override
public boolean equals(Object o) {
if (this == o) return true;
if (o == null || getClass() != o.getClass()) return false;
${entity.name}Base that = (${entity.name}Base) o;
<#assign primaryKey = entity.primaryKey>
<#if primaryKey?has_content>
    return Objects.equals(${primaryKey.get().name}, that.${primaryKey.get().name});
<#else>
    return false;
</#if>
}

@Override
public int hashCode() {
<#assign primaryKey = entity.primaryKey>
<#if primaryKey?has_content>
    return Objects.hash(${primaryKey.get().name});
<#else>
    return super.hashCode();
</#if>
}

@Override
public String toString() {
return "${entity.name}{" +
<#list entity.fields as field>
    "${field.name}=" + ${field.name} +
    <#if field_has_next>", " +</#if>
</#list>
'}';
}
}