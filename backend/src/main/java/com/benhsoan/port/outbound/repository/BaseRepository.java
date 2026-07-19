package com.benhsoan.port.outbound.repository;

import java.util.Optional;

public interface BaseRepository<T, ID> {

    Optional<T> findById(ID id);

    T save(T entity);

    void deleteById(ID id);
}