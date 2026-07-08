package com.shop.util;

import com.shop.model.CartItem;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Tien ich doc/ghi gio hang bang Cookie (thay cho Session).
 *
 * Cach luu:
 *   - Moi CartItem duoc serialize thanh: productId|Base64(name)|price|Base64(image)|quantity
 *   - Cac item ngan cach boi dau ";"
 *   - Toan bo chuoi duoc URL-encode de an toan khi luu vao cookie.
 *   - Cookie co ten "cart", thoi han 7 ngay.
 *
 * Gioi han: Cookie toi da ~4KB, du cho khoang 20 san pham.
 */
public class CartCookieHelper {

    private static final String COOKIE_NAME = "cart";
    private static final int MAX_AGE = 7 * 24 * 60 * 60; // 7 ngay (tinh bang giay)

    // ========================= DOC GIO HANG =========================

    /** Doc gio hang tu cookie. Tra ve list rong neu chua co. */
    public static List<CartItem> getCart(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (COOKIE_NAME.equals(c.getName())) {
                    String value = URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
                    return deserialize(value);
                }
            }
        }
        return new ArrayList<>();
    }

    // ========================= GHI GIO HANG =========================

    /** Luu gio hang vao cookie. Neu gio trong thi xoa cookie. */
    public static void saveCart(HttpServletResponse resp, List<CartItem> cart) {
        if (cart == null || cart.isEmpty()) {
            clearCart(resp);
            return;
        }
        String value = URLEncoder.encode(serialize(cart), StandardCharsets.UTF_8);
        Cookie cookie = new Cookie(COOKIE_NAME, value);
        cookie.setMaxAge(MAX_AGE);
        cookie.setPath("/");
        resp.addCookie(cookie);
    }

    /** Xoa cookie gio hang (set maxAge = 0). */
    public static void clearCart(HttpServletResponse resp) {
        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        resp.addCookie(cookie);
    }

    // ========================= SERIALIZE / DESERIALIZE =========================

    /**
     * Chuyen List<CartItem> thanh chuoi:
     *   productId|Base64(name)|price|Base64(image)|quantity;item2;item3...
     */
    private static String serialize(List<CartItem> cart) {
        StringBuilder sb = new StringBuilder();
        for (CartItem item : cart) {
            if (sb.length() > 0) sb.append(";");
            sb.append(item.getProductId()).append("|")
              .append(encodeB64(item.getName())).append("|")
              .append(item.getPrice().toPlainString()).append("|")
              .append(encodeB64(item.getImage() != null ? item.getImage() : "")).append("|")
              .append(item.getQuantity());
        }
        return sb.toString();
    }

    /** Chuyen chuoi nguoc lai thanh List<CartItem>. */
    private static List<CartItem> deserialize(String data) {
        List<CartItem> cart = new ArrayList<>();
        if (data == null || data.isEmpty()) return cart;
        String[] items = data.split(";");
        for (String itemStr : items) {
            String[] parts = itemStr.split("\\|");
            if (parts.length == 5) {
                try {
                    CartItem item = new CartItem();
                    item.setProductId(Integer.parseInt(parts[0]));
                    item.setName(decodeB64(parts[1]));
                    item.setPrice(new BigDecimal(parts[2]));
                    item.setImage(decodeB64(parts[3]));
                    item.setQuantity(Integer.parseInt(parts[4]));
                    cart.add(item);
                } catch (Exception e) {
                    // Bo qua dong loi, khong lam sap chuong trinh
                    e.printStackTrace();
                }
            }
        }
        return cart;
    }

    // ========================= HELPERS =========================

    private static String encodeB64(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }

    private static String decodeB64(String s) {
        return new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8);
    }
}
