package org.example;

import org.example.entities.Categorie;
import org.example.entities.Commande;
import org.example.entities.Produit;
import org.example.service.CommandeService;
import org.example.service.CrudService;
import org.example.service.ProduitService;
import org.example.util.HibernateUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public final class App {
    private App() { }

    public static void main(String[] args) {
        try {
            CrudService<Categorie, Long> categories = new CrudService<>(Categorie.class);
            ProduitService produits = new ProduitService();

            Optional<Categorie> existante = categories.getAll().stream()
                    .filter(c -> "GAMING".equals(c.getCode()))
                    .findFirst();
            Categorie gaming = existante.orElseGet(() -> {
                Categorie nouvelle = new Categorie(
                        "GAMING", "Accessoires gaming", "Équipements destinés aux joueurs");
                categories.create(nouvelle);
                return nouvelle;
            });

            String suffixe = String.valueOf(System.currentTimeMillis());
            Produit souris = new Produit("MOU-" + suffixe, "Souris RGB",
                    "Souris ergonomique à six boutons", new BigDecimal("349.90"), 15, gaming);
            produits.create(souris);

            Commande commande = new Commande();
            commande.setLivraisonPrevue(LocalDate.now().plusDays(3));
            commande.setCommentaire("Livraison souhaitée dans l'après-midi");
            commande.addProduit(souris, 2);
            new CommandeService().create(commande);

            System.out.println("Commande " + commande.getReference() + " créée avec succès");
            System.out.println("Stock restant pour " + souris.getLibelle() + " : "
                    + produits.getById(souris.getId()).getStock());
        } finally {
            HibernateUtil.shutdown();
        }
    }
}
