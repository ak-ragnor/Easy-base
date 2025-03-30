package ${entity.componentPackage("model")}.listener;

import ${entity.baseComponentPackage("model")}.${entity.name}Base;
import jakarta.persistence.*;
import java.time.Instant;

/**
* Entity lifecycle listener for ${entity.name}.
* Handles entity lifecycle events.
*/
public class ${entity.name}ModelListener {
<#list entity.listeners as listener>
    @${listener.type}
    public void ${listener.method}(${entity.name}Base entity) {
    // CUSTOM CODE START: ${listener.method}
    <#if listener.type == "PrePersist">
        if (entity.getCreatedDate() == null) {
        entity.setCreatedDate(Instant.now());
        }
        entity.setLastModifiedDate(Instant.now());
    <#elseif listener.type == "PreUpdate">
        entity.setLastModifiedDate(Instant.now());
    <#else>
        // Custom implementation for ${listener.method}
    </#if>
    // CUSTOM CODE END: ${listener.method}
    }
</#list>
}