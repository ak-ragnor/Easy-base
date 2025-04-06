package ${entity.componentPackage("service")};

import ${entity.baseComponentPackage("service")}.${entity.name}BaseLocalServiceImpl;
import ${entity.componentPackage("repository")}.${entity.name}JpaRepository;
import ${entity.componentPackage("api")}.model.mapper.${entity.name}Mapper;
import com.easybase.core.sync.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* Local service implementation for ${entity.name}.
* This class extends the base service implementation and can be customized.
*/
@Service
@Transactional
public class ${entity.name}LocalServiceImpl extends ${entity.name}BaseLocalServiceImpl implements ${entity.name}LocalService {

@Autowired
public ${entity.name}LocalServiceImpl(
${entity.name}JpaRepository repository,
${entity.name}Mapper mapper,
SyncService syncService) {
super(repository, mapper, syncService);
}

// CUSTOM CODE START: methods
// Add custom service methods here
// CUSTOM CODE END: methods
}