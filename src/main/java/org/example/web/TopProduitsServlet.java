package org.example.web;

import org.example.dto.ProduitVenduDTO;
import org.example.service.RapportVentesService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.YearMonth;
import java.util.List;

@WebServlet(name = "topProduitsServlet", urlPatterns = "/api/rapports/top-produits")
public class TopProduitsServlet extends HttpServlet {
    private final RapportVentesService service = new RapportVentesService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int limite = lireLimite(request.getParameter("limite"));
        List<ProduitVenduDTO> produits = service.produitsLesPlusVendus(YearMonth.now(), limite);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < produits.size(); i++) {
            if (i > 0) json.append(',');
            ProduitVenduDTO produit = produits.get(i);
            json.append("{\"produitId\":").append(produit.getProduitId())
                    .append(",\"reference\":\"").append(echapper(produit.getReference()))
                    .append("\",\"libelle\":\"").append(echapper(produit.getLibelle()))
                    .append("\",\"quantiteVendue\":").append(produit.getQuantiteVendue()).append('}');
        }
        response.getWriter().write(json.append(']').toString());
    }

    private int lireLimite(String valeur) {
        if (valeur == null || valeur.trim().isEmpty()) return 5;
        try {
            int limite = Integer.parseInt(valeur);
            return Math.max(1, Math.min(limite, 50));
        } catch (NumberFormatException exception) {
            return 5;
        }
    }

    private String echapper(String valeur) {
        return valeur.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\r", "\\r").replace("\n", "\\n");
    }
}
