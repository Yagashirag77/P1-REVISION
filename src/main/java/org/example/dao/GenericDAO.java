package org.example.dao;

import org.example.util.HibernateUtil;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.io.Serializable;
import java.util.List;

public class GenericDAO<T, ID extends Serializable> {
    private final Class<T> entityClass;
    private final EntityManagerFactory emf;

    public GenericDAO(Class<T> entityClass) {
        this(entityClass, HibernateUtil.getEntityManagerFactory());
    }

    public GenericDAO(Class<T> entityClass, EntityManagerFactory emf) {
        if (entityClass == null || emf == null) throw new IllegalArgumentException("Classe et EntityManagerFactory obligatoires");
        this.entityClass = entityClass;
        this.emf = emf;
    }

    public T find(ID id) {
        EntityManager em = emf.createEntityManager();
        try { return em.find(entityClass, id); }
        finally { em.close(); }
    }

    public List<T> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass).getResultList();
        } finally { em.close(); }
    }

    public void create(T entity) {
        executeWrite(em -> { em.persist(entity); return null; });
    }

    public T update(T entity) { return executeWrite(em -> em.merge(entity)); }

    public void delete(T entity) {
        executeWrite(em -> {
            em.remove(em.contains(entity) ? entity : em.merge(entity));
            return null;
        });
    }

    private <R> R executeWrite(EntityOperation<R> operation) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R result = operation.apply(em);
            tx.commit();
            return result;
        } catch (RuntimeException exception) {
            if (tx.isActive()) tx.rollback();
            throw exception;
        } finally { em.close(); }
    }

    @FunctionalInterface
    private interface EntityOperation<R> { R apply(EntityManager em); }
}
