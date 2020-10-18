package com.example.criminalintent.data.repository;

import androidx.lifecycle.LiveData;

import java.io.File;
import java.util.List;
import java.util.UUID;

public interface IRepository<E> {
    LiveData<List<E>> getList();
    LiveData<E> get(UUID uuid);
    void update(E e);
    void delete(E e);
    void insert(E e);
    void insertList(List<E> list);
    int getPosition(UUID uuid);
    File getPhotoFile(E e);
}
