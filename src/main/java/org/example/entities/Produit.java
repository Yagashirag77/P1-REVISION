package org.example.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "produits")
@NamedQueries({
    @NamedQuery(name = "Produit.findAll", query = "SELECT p FROM Produit p"),
    @NamedQuery(name = "Produit.findByCategorie", query = "SELECT p FROM Produit p WHERE p.categorie.id = :catId"),
    @NamedQuery(name = "Produit.searchByLibelle", query = "SELECT p FROM Produit p WHERE LOWER(p.libelle) LIKE LOWER(CONCAT('%', :term, '%'))"),
    @NamedQuery(name = "Produit.findStockFaible", query = "SELECT p FROM Produit p WHERE p.actif = true AND p.stock <= :seuil ORDER BY p.stock")
})
public class Produit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String libelle;

    @Column(nullable = false, unique = true, length = 30)
    private String reference;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private boolean actif = true;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;

    @Column(name = "date_creation", nullable = false)
    private LocalDate dateCreation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    @OneToMany(mappedBy = "produit")
    private List<CommandeProduit> lignesCommande = new ArrayList<>();

    public Produit() { }

    public Produit(String libelle, BigDecimal prix, Categorie categorie) {
        this("PRD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                libelle, null, prix, 0, categorie);
    }

    public Produit(String reference, String libelle, String description,
                   BigDecimal prix, int stock, Categorie categorie) {
        if (stock < 0) throw new IllegalArgumentException("Le stock initial ne peut pas être négatif");
        this.reference = reference;
        this.libelle = libelle;
        this.description = description;
        this.prix = prix;
        this.stock = stock;
        this.categorie = categorie;
        this.dateCreation = LocalDate.now();
    }

    public Long getId() { return id; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getStock() { return stock; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { this.prix = prix; }
    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }

    public void ajouterStock(int quantite) {
        if (quantite <= 0) throw new IllegalArgumentException("La quantité ajoutée doit être positive");
        stock += quantite;
    }

    public void retirerStock(int quantite) {
        if (quantite <= 0 || quantite > stock) throw new IllegalArgumentException("Stock insuffisant");
        stock -= quantite;
    }

    @Override public String toString() {
        return "Produit{id=" + id + ", reference='" + reference + "', libelle='" + libelle
                + "', prix=" + prix + ", stock=" + stock + "}";
    }
}
