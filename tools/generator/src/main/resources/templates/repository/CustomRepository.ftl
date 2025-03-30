package ${entity.componentPackage("repository")};

import ${entity.baseComponentPackage("repository")}.${entity.name}BaseJpaRepository;
import org.springframework.stereotype.Repository;

/**
* Repository interface for ${entity.name}.
* This interface extends the base repository and can be customized.
*/
@Repository
public interface ${entity.name}JpaRepository extends ${entity.name}BaseJpaRepository {
// CUSTOM CODE START: methods
// Add custom repository methods here
// CUSTOM CODE END: methods
}