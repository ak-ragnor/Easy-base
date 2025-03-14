package ${entity.packageName}.model;

import java.util.UUID;
<#-- Import enums -->
<#list entity.fields as field>
<#if field.isEnum()>
import ${entity.packageName}.model.enums.${field.enumClass};
</#if>
</#list>
<#-- Import relationship targets -->
<#list entity.fields as field>
<#if field.isRelationship() && field.targetPackage??>
import ${field.targetPackage}.${field.target};
</#if>
</#list>
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
* Wrapper for ${entity.entity} implementations that provides method interception.
* This class can be used to add functionality like logging, validation, or
* security checks around entity methods.
*
* Generated by EasyBase Code Generator.
*/
@Component
@Primary
public class ${entity.entity}Wrapper implements ${entity.entity} {

private final ${entity.entity} delegate;

@Autowired
public ${entity.entity}Wrapper(${entity.entity}Impl delegate) {
this.delegate = delegate;
    }

    <#-- Delegate all interface methods to the wrapped instance -->
    <#list entity.fields as field>
    @Override
    public ${field.isEnum()?then(field.enumClass, field.isRelationship()?then(field.target, field.javaType))} get${field.name?cap_first}() {
// Pre-processing could be added here
${field.isEnum()?then(field.enumClass, field.isRelationship()?then(field.target, field.javaType))} result = delegate.get${field.name?cap_first}();
        // Post-processing could be added here
        return result;
    }

    @Override
    public void set${field.name?cap_first}(${field.isEnum()?then(field.enumClass, field.isRelationship()?then(field.target, field.javaType))} ${field.name}) {
// Pre-processing could be added here
delegate.set${field.name?cap_first}(${field.name});
        // Post-processing could be added here
    }

    </#list>
}