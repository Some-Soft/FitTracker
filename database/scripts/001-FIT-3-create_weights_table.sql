CREATE SCHEMA fittracker;
CREATE TABLE fittracker.weights (
    id BIGSERIAL PRIMARY KEY,
    value NUMERIC(5,2),
    date DATE UNIQUE
);