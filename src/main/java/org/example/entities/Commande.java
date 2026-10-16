package org.example.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "commandes")
public class Commande {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_commande", nullable = false)
    private LocalDate dateCommande;

    @Column(nullable = false, unique = true, length = 30)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutCommande statut;

    @Column(name = "livraison_prevue")
    private LocalDate livraisonPrevue;

    @Column(length = 500)
    private String commentaire;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommandeProduit> lignes = new ArrayList<>();

    public Commande() {
        this.dateCommande = LocalDate.now();
        this.reference = "CMD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.statut = StatutCommande.EN_PREPARATION;
    }

    public Long getId() { return id; }
    public LocalDate getDateCommande() { return dateCommande; }
    public void setDateCommande(LocalDate dateCommande) { this.dateCommande = dateCommande; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public StatutCommande getStatut() { return statut; }
    public void setStatut(StatutCommande statut) { this.statut = statut; }
    public LocalDate getLivraisonPrevue() { return livraisonPrevue; }
    public void setLivraisonPrevue(LocalDate livraisonPrevue) { this.livraisonPrevue = livraisonPrevue; }
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
    public List<CommandeProduit> getLignes() { return Collections.unmodifiableList(lignes); }

    public void addProduit(Produit produit, int quantite) {
        if (produit == null) throw new IllegalArgumentException("Le produit est obligatoire");
        if (quantite <= 0) throw new IllegalArgumentException("La quantité doit être strictement positive");
        lignes.add(new CommandeProduit(this, produit, quantite));
    }

    public void removeLigne(CommandeProduit ligne) {
        if (lignes.remove(ligne)) ligne.setCommande(null);
    }
}
