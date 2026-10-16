package org.example.service;

import org.example.dto.VenteCategorieDTO;
import org.example.entities.Categorie;
import org.example.entities.Commande;
import org.example.entities.CommandeProduit;
import org.example.entities.Produit;
import org.example.util.HibernateUtil;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class RapportVentesService {
    public List<VenteCategorieDTO> ventesMoisPrecedent() {
        return ventesDuMois(YearMonth.now().minusMonths(1));
    }

    public List<VenteCategorieDTO> ventesDuMois(YearMonth mois) {
        if (mois == null) throw new IllegalArgumentException("Le mois est obligatoire");
        LocalDate debut = mois.atDay(1);
        LocalDate finExclusive = mois.plusMonths(1).atDay(1);
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<VenteCategorieDTO> query = cb.createQuery(VenteCategorieDTO.class);
            Root<CommandeProduit> ligne = query.from(CommandeProduit.class);
            Join<CommandeProduit, Commande> commande = ligne.join("commande");
            Join<CommandeProduit, Produit> produit = ligne.join("produit");
            Join<Produit, Categorie> categorie = produit.join("categorie");

            Expression<BigDecimal> montant = cb.prod(
                    produit.<BigDecimal>get("prix"), ligne.<Integer>get("quantite")).as(BigDecimal.class);
            query.select(cb.construct(VenteCategorieDTO.class,
                            categorie.get("id"), categorie.get("nom"), cb.sum(montant)))
                    .where(cb.greaterThanOrEqualTo(commande.get("dateCommande"), debut),
                           cb.lessThan(commande.get("dateCommande"), finExclusive))
                    .groupBy(categorie.get("id"), categorie.get("nom"))
                    .orderBy(cb.asc(categorie.get("nom")));
            return em.createQuery(query).getResultList();
        } finally { em.close(); }
    }
}
