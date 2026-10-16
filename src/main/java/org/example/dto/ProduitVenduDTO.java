package org.example.dto;

public class ProduitVenduDTO {
    private final Long produitId;
    private final String reference;
    private final String libelle;
    private final Long quantiteVendue;

    public ProduitVenduDTO(Long produitId, String reference, String libelle, Long quantiteVendue) {
        this.produitId = produitId;
        this.reference = reference;
        this.libelle = libelle;
        this.quantiteVendue = quantiteVendue;
    }

    public Long getProduitId() { return produitId; }
    public String getReference() { return reference; }
    public String getLibelle() { return libelle; }
    public Long getQuantiteVendue() { return quantiteVendue; }
}
