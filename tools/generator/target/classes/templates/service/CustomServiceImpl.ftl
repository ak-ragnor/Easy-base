package ${entity.componentPackage("service")};

import ${entity.baseComponentPackage("service")}.${entity.name}BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* Service implementation for ${entity.name}.
* This class extends the base service implementation and can be customized.
*/
@Service
@Transactional
public class ${entity.name}ServiceImpl extends ${entity.name}BaseServiceImpl implements ${entity.name}Service {

@Autowired
public ${entity.name}ServiceImpl(${entity.name}LocalService localService) {
super(localService);
}

// CUSTOM CODE START: methods
// Add custom service methods here
// CUSTOM CODE END: methods
}