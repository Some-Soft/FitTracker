ALTER TABLE fittracker.weights
ADD COLUMN user_id UUID;

ALTER TABLE fittracker.weights
ADD CONSTRAINT fk_weights_users
FOREIGN KEY (user_id) REFERENCES fittracker.users(id);

ALTER TABLE fittracker.weights
DROP CONSTRAINT weights_date_key;

ALTER TABLE fittracker.weights
ADD CONSTRAINT weights_user_date_key UNIQUE (date, user_id);

ALTER TABLE fittracker.weights
ALTER COLUMN id SET NOT NULL,
ALTER COLUMN value SET NOT NULL,
ALTER COLUMN date SET NOT NULL,
ALTER COLUMN user_id SET NOT NULL;