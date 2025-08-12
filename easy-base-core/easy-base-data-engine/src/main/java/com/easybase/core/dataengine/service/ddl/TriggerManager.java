package com.easybase.core.dataengine.service.ddl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TriggerManager {

    private final DSLContext dsl;

    public void createUpdatedAtTrigger(String schema, String table) {
        String functionSql = """
            CREATE OR REPLACE FUNCTION update_updated_at_column()
            RETURNS TRIGGER AS $$
            BEGIN
                NEW.updated_at = now();
                RETURN NEW;
            END;
            $$ language 'plpgsql';
            """;
        dsl.execute(functionSql);

        String triggerName = "update_" + table + "_updated_at";

        dsl.execute("DROP TRIGGER IF EXISTS {0} ON {1}",
                DSL.name(triggerName),
                DSL.table(DSL.name(schema, table))
        );

        dsl.execute("""
            CREATE TRIGGER {0}
            BEFORE UPDATE ON {1}
            FOR EACH ROW
            EXECUTE FUNCTION update_updated_at_column()
            """,
                DSL.name(triggerName),
                DSL.table(DSL.name(schema, table))
        );
    }
}
