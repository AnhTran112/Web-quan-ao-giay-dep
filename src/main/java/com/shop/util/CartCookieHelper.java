package com.shop.util;

import com.shop.dao.ProductDAO;
import com.shop.model.CartItem;
import com.shop.model.Product;
import com.shop.model.ProductVariant;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Tien ich doc/xoa gio hang luu bang Cookie.
 *
 * Format cookie (giong CartServlet):
 *   - Cookie ten "cart", gia tri duoc URL-encode.
 *   - Moi item: productId:variantId:quantity (variantId = 0 neu khong co phan loai).
 *   - Cac item ngan cach boi dau ",".
 *
 * Cookie chi luu id + so luong; ten, gia, anh duoc doc lai tu DB
 * de tranh gia bi sua tu phia client.
 */
public class CartCookieHelper {

    private static final String COOKIE_NAME = "cart";

    private static final ProductDAO productDAO = new ProductDAO();

    // ========================= DOC GIO HANG =========================

    /** Doc gio hang tu cookie, load thong tin san pham tu DB. Tra ve list rong neu chua co. */
    public static List<CartItem> getCart(HttpServletRequest req) {
        List<CartItem> cart = new ArrayList<>();
        String raw = getRawCookie(req);
        if (raw == null || raw.trim().isEmpty()) {
            return cart;
        }

        for (String itemStr : raw.split(",")) {
            String[] parts = itemStr.trim().split(":");
            if (parts.length != 3) continue;

            int pId = parseInt(parts[0], 0);
            int vId = parseInt(parts[1], 0);
            int qty = parseInt(parts[2], 0);
            if (pId <= 0 || qty <= 0) continue;

            Product p = productDAO.getById(pId);
            if (p == null) continue;

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

            // Ap dung giam gia neu co
            if (p.getDiscountPercent() > 0) {
                BigDecimal discountMulti = BigDecimal.valueOf(100 - p.getDiscountPercent())
                        .divide(BigDecimal.valueOf(100));
                price = price.multiply(discountMulti);
            }

            cart.add(new CartItem(pId, variantIdObj, p.getName(), variantName, price, p.getImage(), qty));
        }
        return cart;
    }

    // ========================= XOA GIO HANG =========================

    /** Xoa cookie gio hang (set maxAge = 0). Dung sau khi dat hang thanh cong. */
    public static void clearCart(HttpServletResponse resp) {
        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        resp.addCookie(cookie);
    }

    // ========================= HELPERS =========================

    private static String getRawCookie(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (COOKIE_NAME.equals(c.getName())) {
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

    private static int parseInt(String s, int def) {
        try {
            return (s == null || s.isEmpty()) ? def : Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }
}
