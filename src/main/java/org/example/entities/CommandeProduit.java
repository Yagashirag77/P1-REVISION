package org.example.entities;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "commande_produit")
public class CommandeProduit {
    @EmbeddedId
    private CommandeProduitPK id = new CommandeProduitPK();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("commandeId")
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("produitId")
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @Column(nullable = false)
    private int quantite;

    @Column(name = "prix_unitaire", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    public CommandeProduit() { }
    public CommandeProduit(Commande commande, Produit produit, int quantite) {
        this.commande = commande;
        this.produit = produit;
        this.quantite = quantite;
        this.prixUnitaire = produit.getPrix();
    }

    public CommandeProduitPK getId() { return id; }
    public Commande getCommande() { return commande; }
    public void setCommande(Commande commande) { this.commande = commande; }
    public Produit getProduit() { return produit; }
    public void setProduit(Produit produit) { this.produit = produit; }
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) {
        if (quantite <= 0) throw new IllegalArgumentException("La quantité doit être strictement positive");
        this.quantite = quantite;
    }
    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        if (prixUnitaire == null || prixUnitaire.signum() < 0) {
            throw new IllegalArgumentException("Le prix unitaire doit être positif");
        }
        this.prixUnitaire = prixUnitaire;
    }
    public BigDecimal getMontant() { return prixUnitaire.multiply(BigDecimal.valueOf(quantite)); }
}
