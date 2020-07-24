package com.example.criminalintent.repository;

import java.util.List;
import java.util.UUID;

public interface RepositoryInterface<E> {
    List<E> getList();
    E get(UUID uuid);
    void setList(List<E> list);
    void update(E e);
    void delete(E e);
    void insert(E e);
    void insertList(List<E> list);
}
