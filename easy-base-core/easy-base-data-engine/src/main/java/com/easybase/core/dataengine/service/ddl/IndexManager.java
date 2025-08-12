package com.easybase.core.dataengine.service.ddl;

import com.easybase.core.dataengine.enums.AttributeType;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IndexManager {

    private final DSLContext dsl;

    public void createGinIndexIfNotExists(String schema, String table) {
        String indexName = table + "_data_gin_idx";

        dsl.execute("CREATE INDEX IF NOT EXISTS {0} ON {1} USING GIN (data)",
                DSL.name(indexName),
                DSL.table(DSL.name(schema, table))
        );
    }

    public void createAttributeIndexIfNotExists(String schema, String table, String attributeName, AttributeType type) {
        String indexName = String.format("%s_%s_idx", table, attributeName);
        String castType = type.getPostgresType();

        dsl.execute(
                "CREATE INDEX IF NOT EXISTS {0} ON {1}.{2} (((data->>'{3}')::{4}))",
                DSL.name(indexName),
                DSL.name(schema),
                DSL.name(table),
                DSL.inline(type),
                DSL.keyword(castType)
        );
    }
}
