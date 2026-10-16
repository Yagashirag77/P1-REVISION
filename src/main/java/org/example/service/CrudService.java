package org.example.service;

import org.example.dao.GenericDAO;
import java.io.Serializable;
import java.util.List;

public class CrudService<T, ID extends Serializable> {
    private final GenericDAO<T, ID> dao;

    public CrudService(Class<T> entityClass) { this.dao = new GenericDAO<>(entityClass); }
    public T getById(ID id) { return dao.find(require(id)); }
    public List<T> getAll() { return dao.findAll(); }
    public void create(T entity) { dao.create(require(entity)); }
    public T update(T entity) { return dao.update(require(entity)); }
    public void delete(T entity) { dao.delete(require(entity)); }

    private <V> V require(V value) {
        if (value == null) throw new IllegalArgumentException("La valeur ne doit pas être nulle");
        return value;
    }
}
