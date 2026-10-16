package org.example.service;

import org.example.entities.Commande;
import org.example.entities.CommandeProduit;
import org.example.entities.Produit;
import org.example.util.HibernateUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

// Une commande et ses lignes sont enregistrées dans la même unité de travail.
public class CommandeService extends CrudService<Commande, Long> {
    public CommandeService() { super(Commande.class); }

    @Override
    public void create(Commande commande) {
        if (commande == null) throw new IllegalArgumentException("La commande ne doit pas être nulle");
        if (commande.getLignes().isEmpty()) throw new IllegalArgumentException("Une commande doit contenir au moins une ligne");
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            for (CommandeProduit ligne : commande.getLignes()) {
                if (ligne.getProduit().getId() == null) {
                    throw new IllegalArgumentException("Chaque produit doit déjà être persisté");
                }
                Produit produitGere = em.find(Produit.class, ligne.getProduit().getId());
                if (produitGere == null || !produitGere.isActif()) {
                    throw new IllegalArgumentException("Le produit demandé est introuvable ou inactif");
                }
                if (produitGere.getStock() < ligne.getQuantite()) {
                    throw new IllegalArgumentException("Stock insuffisant pour " + produitGere.getLibelle());
                }
                produitGere.retirerStock(ligne.getQuantite());
                ligne.setProduit(produitGere);
                ligne.setPrixUnitaire(produitGere.getPrix());
            }
            em.persist(commande);
            tx.commit();
        } catch (RuntimeException exception) {
            if (tx.isActive()) tx.rollback();
            throw exception;
        } finally { em.close(); }
    }
}
