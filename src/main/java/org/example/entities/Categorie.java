package org.example.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.LocalDate;

@Entity
@Table(name = "categories")
public class Categorie {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(length = 300)
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "date_creation", nullable = false)
    private LocalDate dateCreation = LocalDate.now();

    @OneToMany(mappedBy = "categorie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Produit> produits = new ArrayList<>();

    public Categorie() { }
    public Categorie(String nom) {
        this(nom.toUpperCase().replaceAll("[^A-Z0-9]", ""), nom, null);
    }

    public Categorie(String code, String nom, String description) {
        this.code = code;
        this.nom = nom;
        this.description = description;
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDate getDateCreation() { return dateCreation; }
    public List<Produit> getProduits() { return Collections.unmodifiableList(produits); }

    public void addProduit(Produit produit) {
        if (produit != null && !produits.contains(produit)) {
            produits.add(produit);
            produit.setCategorie(this);
        }
    }

    public void removeProduit(Produit produit) {
        if (produits.remove(produit)) produit.setCategorie(null);
    }

    @Override public String toString() {
        return "Categorie{id=" + id + ", code='" + code + "', nom='" + nom + "', active=" + active + "}";
    }
}
