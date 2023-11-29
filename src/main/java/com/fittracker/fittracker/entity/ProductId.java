package com.fittracker.fittracker.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ProductId implements Serializable {

    private UUID id;

    private int version;

    public ProductId() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public ProductId(UUID id, int version) {
        this.id = id;
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProductId productId = (ProductId) o;
        return version == productId.version && Objects.equals(id, productId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, version);
    }
}
