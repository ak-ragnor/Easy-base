ALTER TABLE eb_roles
  ADD COLUMN IF NOT EXISTS search_vector tsvector
  GENERATED ALWAYS AS (
        setweight(to_tsvector('simple', coalesce(name, '')), 'A')
    ||  setweight(to_tsvector('simple', coalesce(description, '')), 'B')
  ) STORED;

CREATE INDEX IF NOT EXISTS eb_roles_search_vector_gin_idx
  ON eb_roles USING GIN (search_vector);
