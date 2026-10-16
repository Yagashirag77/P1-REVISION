package org.example.dto;

import java.math.BigDecimal;

public class VenteCategorieDTO {
    private final Long categorieId;
    private final String categorie;
    private final BigDecimal chiffreAffaires;

    public VenteCategorieDTO(Long categorieId, String categorie, BigDecimal chiffreAffaires) {
        this.categorieId = categorieId;
        this.categorie = categorie;
        this.chiffreAffaires = chiffreAffaires;
    }

    public Long getCategorieId() { return categorieId; }
    public String getCategorie() { return categorie; }
    public BigDecimal getChiffreAffaires() { return chiffreAffaires; }
}
