package com.common.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BaseService<R extends JpaRepository<E, ID>, E, ID> {
    R getRepository();

    default E findById(ID id) {
        return getRepository()
                .findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Entity not found with id: " + id));
    }

    default List<E> findAll() {
        return getRepository().findAll();
    }

    default E save(E entity) {
        return getRepository().save(entity);
    }

    default E update(E entity) {
        return getRepository().save(entity);
    }

    default void delete(ID id) {
        if (!getRepository().existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException(
                    "Cannot delete — entity not found with id: " + id);
        }
        getRepository().deleteById(id);
    }
}
