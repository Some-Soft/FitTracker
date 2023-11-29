CREATE TABLE fittracker.products (
    id UUID,
    version INTEGER DEFAULT 0,
    name VARCHAR(64) NOT NULL,
    kcal NUMERIC(4, 0),
    carbs NUMERIC(4, 0),
    protein NUMERIC(4, 0),
    fat NUMERIC(4, 0),
    user_id UUID NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id, version),
    FOREIGN KEY (user_id) REFERENCES fittracker.users(id)
);