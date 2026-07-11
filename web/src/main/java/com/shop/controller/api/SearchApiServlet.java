package com.shop.controller.api;

import com.shop.dao.ProductDAO;
import com.shop.model.Product;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/search")
public class SearchApiServlet extends HttpServlet {
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        String q = req.getParameter("q");
        PrintWriter out = resp.getWriter();
        
        if (q == null || q.trim().length() < 2) {
            out.write("[]");
            return;
        }

        List<Product> products = productDAO.search(q.trim());
        // Return up to 5 suggestions
        int limit = Math.min(5, products.size());
        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < limit; i++) {
            Product p = products.get(i);
            json.append("{");
            json.append("\"id\":").append(p.getId()).append(",");
            json.append("\"name\":\"").append(escapeJson(p.getName())).append("\",");
            json.append("\"image\":\"").append(escapeJson(p.getImage())).append("\",");
            json.append("\"price\":").append(p.getPrice());
            json.append("}");
            if (i < limit - 1) json.append(",");
        }
        json.append("]");
        
        out.write(json.toString());
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
