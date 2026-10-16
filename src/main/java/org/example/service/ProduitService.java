package org.example.service;

import org.example.entities.Produit;
import org.example.util.HibernateUtil;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProduitService extends CrudService<Produit, Long> {
    public ProduitService() { super(Produit.class); }

    public List<Produit> findByCategorie(Long categorieId) {
        EntityManager em = entityManager();
        try {
            return em.createNamedQuery("Produit.findByCategorie", Produit.class)
                    .setParameter("catId", categorieId)
                    .getResultList();
        } finally { em.close(); }
    }

    // Le JOIN FETCH garde la catégorie disponible lorsque le service rend la main.
    public Produit findByIdWithCategorie(Long id) {
        EntityManager em = entityManager();
        try {
            List<Produit> result = em.createQuery(
                            "SELECT p FROM Produit p JOIN FETCH p.categorie WHERE p.id = :id", Produit.class)
                    .setParameter("id", id)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        } finally { em.close(); }
    }

    public List<Produit> search(String term, BigDecimal minPrix, BigDecimal maxPrix) {
        if (minPrix != null && maxPrix != null && minPrix.compareTo(maxPrix) > 0) {
            throw new IllegalArgumentException("Le prix minimal ne peut pas dépasser le prix maximal");
        }
        EntityManager em = entityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Produit> query = cb.createQuery(Produit.class);
            Root<Produit> produit = query.from(Produit.class);
            List<Predicate> filters = new ArrayList<>();
            if (term != null && !term.trim().isEmpty()) {
                filters.add(cb.like(cb.lower(produit.get("libelle")), "%" + term.trim().toLowerCase() + "%"));
            }
            if (minPrix != null) filters.add(cb.greaterThanOrEqualTo(produit.get("prix"), minPrix));
            if (maxPrix != null) filters.add(cb.lessThanOrEqualTo(produit.get("prix"), maxPrix));
            query.select(produit).where(filters.toArray(new Predicate[0])).orderBy(cb.asc(produit.get("id")));
            return em.createQuery(query).getResultList();
        } finally { em.close(); }
    }

    public List<Produit> findPage(int pageNumber, int pageSize) {
        if (pageNumber < 1 || pageSize < 1) throw new IllegalArgumentException("La page et sa taille doivent être positives");
        EntityManager em = entityManager();
        try {
            return em.createQuery("SELECT p FROM Produit p ORDER BY p.id", Produit.class)
                    .setFirstResult((pageNumber - 1) * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();
        } finally { em.close(); }
    }

    public List<Produit> findStockFaible(int seuil) {
        if (seuil < 0) throw new IllegalArgumentException("Le seuil ne peut pas être négatif");
        EntityManager em = entityManager();
        try {
            return em.createNamedQuery("Produit.findStockFaible", Produit.class)
                    .setParameter("seuil", seuil)
                    .getResultList();
        } finally { em.close(); }
    }

    private EntityManager entityManager() {
        return HibernateUtil.getEntityManagerFactory().createEntityManager();
    }
}
