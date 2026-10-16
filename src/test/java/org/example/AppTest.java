package org.example;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.example.dto.VenteCategorieDTO;
import org.example.entities.Categorie;
import org.example.entities.Client;
import org.example.entities.Commande;
import org.example.entities.Produit;
import org.example.entities.StatutCommande;
import org.example.entities.User;
import org.example.service.CrudService;
import org.example.service.CommandeService;
import org.example.service.ProduitService;
import org.example.service.RapportVentesService;
import org.example.util.HibernateUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class AppTest extends TestCase {
    private static CrudService<Categorie, Long> categories;
    private static ProduitService produits;

    private static void init() {
        categories = new CrudService<>(Categorie.class);
        produits = new ProduitService();
    }

    public static Test suite() {
        return new TestSetup(new TestSuite(AppTest.class)) {
            @Override protected void setUp() { init(); }
            @Override protected void tearDown() { HibernateUtil.shutdown(); }
        };
    }

    public void testCreeEtRechercheUnProduitAvecSaCategorie() {
        Categorie categorie = new Categorie("PAP", "Papeterie", "Fournitures de bureau");
        categories.create(categorie);
        Produit stylo = new Produit("STY-BLEU", "Stylo bleu", "Stylo à bille",
                new BigDecimal("1.20"), 8, categorie);
        stylo.setSeuilAlerte(10);
        produits.create(stylo);

        Produit fetched = produits.findByIdWithCategorie(stylo.getId());
        assertNotNull(fetched);
        assertEquals("Papeterie", fetched.getCategorie().getNom());
        assertEquals(1, produits.search("stylo", BigDecimal.ONE, new BigDecimal("2.00")).size());
        assertFalse(produits.findByCategorie(categorie.getId()).isEmpty());
        assertTrue(produits.findStockFaible(10).stream()
                .anyMatch(produit -> "STY-BLEU".equals(produit.getReference())));
        assertTrue(produits.findAReapprovisionner().stream()
                .anyMatch(produit -> "STY-BLEU".equals(produit.getReference())));
    }

    public void testCalculeLeChiffreAffairesDuMoisPrecedent() {
        Categorie categorie = new Categorie("AUD", "Audio", "Casques et haut-parleurs");
        categories.create(categorie);
        Produit casque = new Produit("CSQ-50", "Casque", "Casque filaire",
                new BigDecimal("50.00"), 10, categorie);
        produits.create(casque);

        Commande commande = new Commande();
        commande.setDateCommande(YearMonth.now().minusMonths(1).atDay(10));
        commande.addProduit(casque, 3);
        new CommandeService().create(commande);
        assertEquals(7, produits.getById(casque.getId()).getStock());

        Produit casqueGere = produits.getById(casque.getId());
        casqueGere.setPrix(new BigDecimal("79.00"));
        produits.update(casqueGere);

        List<VenteCategorieDTO> rapport = new RapportVentesService().ventesMoisPrecedent();
        VenteCategorieDTO audio = rapport.stream()
                .filter(item -> "Audio".equals(item.getCategorie()))
                .findFirst().orElseThrow(AssertionError::new);
        assertEquals(0, new BigDecimal("150.00").compareTo(audio.getChiffreAffaires()));
        assertEquals("CSQ-50", new RapportVentesService()
                .produitsLesPlusVendus(YearMonth.now().minusMonths(1), 3)
                .get(0).getReference());
    }

    public void testPersisteUneSousClasseAvecHeritageJoined() {
        CrudService<Client, Long> clients = new CrudService<>(Client.class);
        Client client = new Client("Amina", LocalDate.now());
        clients.create(client);
        assertNotNull(client.getId());
        assertEquals(1, new CrudService<User, Long>(User.class).getAll().size());
    }

    public void testTransitionsMetierCommande() {
        Commande commande = new Commande();
        commande.valider();
        assertEquals(StatutCommande.VALIDEE, commande.getStatut());
        commande.expedier();
        assertEquals(StatutCommande.EXPEDIEE, commande.getStatut());
        commande.livrer();
        assertEquals(StatutCommande.LIVREE, commande.getStatut());
    }
}
