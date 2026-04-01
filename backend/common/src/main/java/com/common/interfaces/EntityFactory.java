package com.common.interfaces;

/**
 * Generic factory interface for creating entities from DTOs.
 * It takes the entity to create and the dto
 */
public interface EntityFactory<T, D> {
    T create(D dto);
}
