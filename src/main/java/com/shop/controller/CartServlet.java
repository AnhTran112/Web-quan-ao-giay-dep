package com.shop.controller;

import com.shop.dao.ProductDAO;
import com.shop.model.CartItem;
import com.shop.model.Product;
import com.shop.model.ProductVariant;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Quan ly gio hang bang Cookie. URL: "/cart"
 * Format cookie: productId:variantId:quantity,productId:variantId:quantity
 * variantId = 0 neu khong co.
 */
@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, javax.servlet.ServletException {

        String action = req.getParameter("action");
        if ("remove".equals(action)) {
            handleRemove(req, resp);
            return;
        }

        // Hien thi gio hang
        List<CartItem> cart = getCartFromCookie(req);
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cart) {
            total = total.add(item.getSubtotal());
        }

        req.setAttribute("cart", cart);
        req.setAttribute("total", total);
        req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, javax.servlet.ServletException {
        String action = req.getParameter("action");

        if (action == null) {
            action = "";
        }

        switch (action) {
            case "add":
                handleAdd(req, resp);
                break;
            case "update":
                handleUpdate(req, resp);
                break;
            default:
                doGet(req, resp);
                break;
        }
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int productId = parseInt(req.getParameter("productId"), 0);
        int variantId = parseInt(req.getParameter("variantId"), 0);
        int quantity = parseInt(req.getParameter("quantity"), 1);

        String rawCookie = getRawCookie(req);
        List<String> items = parseRawCookie(rawCookie);

        boolean found = false;
        String matchPrefix = productId + ":" + variantId + ":";
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
        resp.sendRedirect(req.getContextPath() + "/cart");
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int productId = parseInt(req.getParameter("productId"), 0);
        int variantId = parseInt(req.getParameter("variantId"), 0);
        int quantity = parseInt(req.getParameter("quantity"), 1);

        String rawCookie = getRawCookie(req);
        List<String> items = parseRawCookie(rawCookie);

        String matchPrefix = productId + ":" + variantId + ":";
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).startsWith(matchPrefix)) {
                if (quantity <= 0) {
                    items.remove(i);
                } else {
                    items.set(i, matchPrefix + quantity);
                }
                break;
            }
        }

        saveCookie(resp, items);
        resp.sendRedirect(req.getContextPath() + "/cart");
    }

    private void handleRemove(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int productId = parseInt(req.getParameter("productId"), 0);
        int variantId = parseInt(req.getParameter("variantId"), 0);

        String rawCookie = getRawCookie(req);
        List<String> items = parseRawCookie(rawCookie);

        String matchPrefix = productId + ":" + variantId + ":";
        items.removeIf(item -> item.startsWith(matchPrefix));

        saveCookie(resp, items);
        resp.sendRedirect(req.getContextPath() + "/cart");
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
        if (raw == null || raw.trim().isEmpty())
            return list;
        String[] parts = raw.split(",");
        for (String p : parts) {
            if (!p.trim().isEmpty())
                list.add(p.trim());
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

    private List<CartItem> getCartFromCookie(HttpServletRequest req) {
        List<CartItem> cart = new ArrayList<>();
        String rawCookie = getRawCookie(req);
        List<String> items = parseRawCookie(rawCookie);

        for (String itemStr : items) {
            String[] parts = itemStr.split(":");
            if (parts.length == 3) {
                int pId = parseInt(parts[0], 0);
                int vId = parseInt(parts[1], 0);
                int qty = parseInt(parts[2], 0);

                Product p = productDAO.getById(pId);
                if (p != null) {
                    BigDecimal price = p.getPrice();
                    String variantName = null;
                    Integer variantIdObj = null;

                    if (vId > 0 && p.getVariants() != null) {
                        for (ProductVariant v : p.getVariants()) {
                            if (v.getId() == vId) {
                                price = v.getPrice();
                                variantName = v.getName();
                                variantIdObj = v.getId();
                                break;
                            }
                        }
                    }

                    // Apply discount
                    if (p.getDiscountPercent() > 0) {
                        BigDecimal discountMulti = BigDecimal.valueOf(100 - p.getDiscountPercent())
                                .divide(BigDecimal.valueOf(100));
                        price = price.multiply(discountMulti);
                    }

                    CartItem ci = new CartItem(pId, variantIdObj, p.getName(), variantName, price, p.getImage(), qty);
                    cart.add(ci);
                }
            }
        }
        return cart;
    }

    private int parseInt(String s, int def) {
        try {
            return (s == null || s.isEmpty()) ? def : Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }
}
