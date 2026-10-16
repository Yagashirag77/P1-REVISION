package org.example.web;

import org.example.dto.VenteCategorieDTO;
import org.example.service.RapportVentesService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "rapportVentesServlet", urlPatterns = "/api/rapports/ventes-mois-precedent")
public class RapportVentesServlet extends HttpServlet {
    private final RapportVentesService service = new RapportVentesService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        List<VenteCategorieDTO> ventes = service.ventesMoisPrecedent();
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < ventes.size(); i++) {
            if (i > 0) json.append(',');
            VenteCategorieDTO vente = ventes.get(i);
            json.append("{\"categorieId\":").append(vente.getCategorieId())
                    .append(",\"categorie\":\"").append(escape(vente.getCategorie()))
                    .append("\",\"chiffreAffaires\":").append(vente.getChiffreAffaires()).append('}');
        }
        response.getWriter().write(json.append(']').toString());
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\r", "\\r").replace("\n", "\\n");
    }
}
