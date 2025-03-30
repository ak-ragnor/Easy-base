package ${entity.componentPackage("model")};

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import ${entity.baseComponentPackage("model")}.${entity.name}Base;

/**
* Entity class for ${entity.name}.
* This class extends the base class and can be customized.
*/
@Entity
@Table(name = "${entity.fullTableName}")
public class ${entity.name} extends ${entity.name}Base {
    // CUSTOM CODE START: fields
    // Add custom fields here
    // CUSTOM CODE END: fields

    // CUSTOM CODE START: methods
    // Add custom methods here
    // CUSTOM CODE END: methods
}