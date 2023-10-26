CREATE TABLE fittracker.users (
    id UUID PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    email VARCHAR(254) NOT NULL UNIQUE,
    password VARCHAR(80) NOT NULL
);
