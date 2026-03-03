package com.service;

import java.util.List;

public interface BaseService<T, ID, DTO> {
    List<T> findAll();
    List<T> search(String keyword);
    T findById(ID id);
    T save(DTO dto);
    T update(ID id, DTO dto);
    void delete(ID id);
}
