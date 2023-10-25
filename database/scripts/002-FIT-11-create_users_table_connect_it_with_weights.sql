CREATE TABLE fittracker.users (
    id UUID PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    email VARCHAR(254) NOT NULL UNIQUE,
    password VARCHAR(80) NOT NULL
);

ALTER TABLE fittracker.weights
ADD user_id UUID NOT NULL;

ALTER TABLE fittracker.weights
ADD CONSTRAINT fk_user_id
FOREIGN KEY (user_id) REFERENCES fittracker.users (id);

ALTER TABLE fittracker.weights
DROP CONSTRAINT IF EXISTS unique_date;

ALTER TABLE fittracker.weights
ADD CONSTRAINT unique_date_user_id UNIQUE (date, user_id);