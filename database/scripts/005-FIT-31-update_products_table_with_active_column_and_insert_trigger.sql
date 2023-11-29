ALTER TABLE fittracker.products
ADD COLUMN active BOOLEAN DEFAULT true NOT NULL;

CREATE OR REPLACE FUNCTION fittracker.product_insert_trigger()
RETURNS TRIGGER AS $$
DECLARE
    new_version INTEGER;
    existing_product_id UUID;
BEGIN
    WITH updated_product AS (
            UPDATE fittracker.products
            SET active = false
            WHERE name = NEW.name AND user_id = NEW.user_id AND active
            RETURNING id, version
    )
    SELECT id, version + 1 INTO existing_product_id, new_version FROM updated_product;

    IF new_version IS NOT NULL THEN
        NEW.version := new_version;
        NEW.id := existing_product_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER product_before_insert
BEFORE INSERT ON fittracker.products
FOR EACH ROW EXECUTE FUNCTION fittracker.product_insert_trigger();