package com.shop.controller.api;

import com.shop.dao.CategoryDAO;
import com.shop.dao.ProductDAO;
import com.shop.model.Category;
import com.shop.model.Product;
import com.shop.model.ProductVariant;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/product/quickview")
public class QuickViewApiServlet extends HttpServlet {
    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        int id = parseInt(req.getParameter("id"), 0);
        if (id <= 0) {
            out.write("{\"success\":false}");
            return;
        }

        Product p = productDAO.getById(id);
        if (p == null) {
            out.write("{\"success\":false}");
            return;
        }

        String catName = "";
        for (Category cat : categoryDAO.getAll()) {
            if (cat.getId() == p.getCategoryId()) {
                catName = cat.getName();
                break;
            }
        }

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"success\":true,");
        json.append("\"id\":").append(p.getId()).append(",");
        json.append("\"name\":\"").append(escapeJson(p.getName())).append("\",");
        json.append("\"image\":\"").append(escapeJson(p.getImage())).append("\",");
        json.append("\"category\":\"").append(escapeJson(catName)).append("\",");
        json.append("\"price\":").append(p.getPrice()).append(",");
        json.append("\"discountPercent\":").append(p.getDiscountPercent()).append(",");
        json.append("\"quantity\":").append(p.getQuantity()).append(",");
        json.append("\"hasVariants\":").append(p.isHasVariants()).append(",");
        
        if (p.isHasVariants() && p.getVariants() != null && !p.getVariants().isEmpty()) {
            json.append("\"minPrice\":").append(p.getMinPrice()).append(",");
            json.append("\"maxPrice\":").append(p.getMaxPrice()).append(",");
            json.append("\"variants\":[");
            List<ProductVariant> vars = p.getVariants();
            for (int i = 0; i < vars.size(); i++) {
                ProductVariant v = vars.get(i);
                json.append("{");
                json.append("\"id\":").append(v.getId()).append(",");
                json.append("\"name\":\"").append(escapeJson(v.getName())).append("\",");
                json.append("\"price\":").append(v.getPrice()).append(",");
                json.append("\"quantity\":").append(v.getQuantity());
                json.append("}");
                if (i < vars.size() - 1) json.append(",");
            }
            json.append("]");
        } else {
            json.append("\"variants\":[]");
        }
        
        json.append("}");
        out.write(json.toString());
    }

    private int parseInt(String s, int def) {
        try { return (s == null || s.isEmpty()) ? def : Integer.parseInt(s); }
        catch (Exception e) { return def; }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
