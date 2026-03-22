ALTER TABLE eb_users
  ADD COLUMN IF NOT EXISTS search_vector tsvector
  GENERATED ALWAYS AS (
        setweight(to_tsvector('simple', coalesce(first_name, '')), 'A')
    ||  setweight(to_tsvector('simple', coalesce(last_name, '')), 'A')
    ||  setweight(to_tsvector('simple', coalesce(display_name, '')), 'B')
    ||  setweight(to_tsvector('simple', coalesce(email, '')), 'C')
  ) STORED;

CREATE INDEX IF NOT EXISTS eb_users_search_vector_gin_idx
  ON eb_users USING GIN (search_vector);
