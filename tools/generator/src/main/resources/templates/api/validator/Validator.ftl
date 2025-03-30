package ${entity.componentPackage("api")}.validator;

import ${entity.componentPackage("api")}.model.dto.${entity.name}RequestDTO;
import ${entity.componentPackage("repository")}.${entity.name}JpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
* Validator for ${entity.name} requests.
* This class validates ${entity.name} request DTOs beyond simple annotation validations.
*/
@Component
public class ${entity.name}Validator implements Validator {

private final ${entity.name}JpaRepository repository;

@Autowired
public ${entity.name}Validator(${entity.name}JpaRepository repository) {
this.repository = repository;
}

@Override
public boolean supports(Class<?> clazz) {
return ${entity.name}RequestDTO.class.isAssignableFrom(clazz);
}

@Override
public void validate(Object target, Errors errors) {
${entity.name}RequestDTO dto = (${entity.name}RequestDTO) target;

// CUSTOM CODE START: validate
// Add custom validation logic here
<#list entity.fields as field>
    <#if field.unique>
        // Check if ${field.name} is unique
        if (dto.get${field.name?cap_first}() != null && repository.existsBy${field.name?cap_first}(dto.get${field.name?cap_first}())) {
        errors.rejectValue("${field.name}", "duplicate", "${field.name?cap_first} already exists");
        }
    </#if>
</#list>
// CUSTOM CODE END: validate
}
}