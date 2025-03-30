package ${entity.componentPackage("api")};

import ${entity.baseComponentPackage("api")}.${entity.name}BaseController;
import ${entity.componentPackage("service")}.${entity.name}Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* Controller for ${entity.name}.
* This class extends the base controller and can be customized.
*/
@RestController
@RequestMapping("/api/${entity.table}")
public class ${entity.name}Controller extends ${entity.name}BaseController {

@Autowired
public ${entity.name}Controller(${entity.name}Service service) {
super(service);
}

// CUSTOM CODE START: methods
// Add custom controller methods here
// CUSTOM CODE END: methods
}