package com.shop.controller.api;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import com.shop.dao.ProductDAO;
import com.shop.model.Product;
import com.shop.model.ProductVariant;

import java.util.ArrayList;
import java.util.List;

@WebServlet("/api/cart/add")
public class CartApiServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        int productId = parseInt(req.getParameter("productId"), 0);
        int variantId = parseInt(req.getParameter("variantId"), 0);
        int quantity = parseInt(req.getParameter("quantity"), 1);

        if (productId <= 0) {
            resp.getWriter().write("{\"success\":false,\"message\":\"Invalid product\"}");
            return;
        }

        ProductDAO productDAO = new ProductDAO();
        Product p = productDAO.getById(productId);
        if (p == null) {
            resp.getWriter().write("{\"success\":false,\"message\":\"Sản phẩm không tồn tại.\"}");
            return;
        }

        int stock = p.getQuantity();
        if (variantId > 0 && p.getVariants() != null) {
            boolean variantFound = false;
            for (ProductVariant v : p.getVariants()) {
                if (v.getId() == variantId) {
                    stock = v.getQuantity();
                    variantFound = true;
                    break;
                }
            }
            if (!variantFound) {
                resp.getWriter().write("{\"success\":false,\"message\":\"Phân loại không hợp lệ.\"}");
                return;
            }
        }

        String rawCookie = getRawCookie(req);
        List<String> items = parseRawCookie(rawCookie);

        int currentQty = 0;
        String matchPrefix = productId + ":" + variantId + ":";
        for (String item : items) {
            if (item.startsWith(matchPrefix)) {
                String[] parts = item.split(":");
                if (parts.length == 3) {
                    currentQty = parseInt(parts[2], 0);
                }
                break;
            }
        }

        if (currentQty + quantity > stock) {
            resp.getWriter().write("{\"success\":false,\"message\":\"Kho không đủ (còn " + stock + " sp).\"}");
            return;
        }
        boolean found = false;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).startsWith(matchPrefix)) {
                String[] parts = items.get(i).split(":");
                if (parts.length == 3) {
                    int oldQty = parseInt(parts[2], 0);
                    items.set(i, matchPrefix + (oldQty + quantity));
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            items.add(productId + ":" + variantId + ":" + quantity);
        }

        saveCookie(resp, items);

        // Calculate total items
        int totalItems = 0;
        for (String itemStr : items) {
            String[] parts = itemStr.split(":");
            if (parts.length == 3) {
                totalItems += parseInt(parts[2], 0);
            }
        }

        resp.getWriter().write("{\"success\":true,\"totalItems\":" + totalItems + "}");
    }

    private String getRawCookie(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("cart".equals(c.getName())) {
                    try {
                        return URLDecoder.decode(c.getValue(), "UTF-8");
                    } catch (Exception e) {
                        return "";
                    }
                }
            }
        }
        return "";
    }

    private List<String> parseRawCookie(String raw) {
        List<String> list = new ArrayList<>();
        if (raw == null || raw.trim().isEmpty()) return list;
        String[] parts = raw.split(",");
        for (String p : parts) {
            if (!p.trim().isEmpty()) list.add(p.trim());
        }
        return list;
    }

    private void saveCookie(HttpServletResponse resp, List<String> items) {
        String raw = String.join(",", items);
        try {
            Cookie c = new Cookie("cart", URLEncoder.encode(raw, "UTF-8"));
            c.setMaxAge(60 * 60 * 24 * 7); // 7 days
            c.setPath("/");
            resp.addCookie(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int parseInt(String s, int def) {
        try { return (s == null || s.isEmpty()) ? def : Integer.parseInt(s); }
        catch (Exception e) { return def; }
    }
}
