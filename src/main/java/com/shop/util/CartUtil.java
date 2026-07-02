package com.shop.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class CartUtil {

    private static final String COOKIE_NAME = "shop_cart";
    private static final int MAX_AGE = 7 * 24 * 60 * 60; // 7 days in seconds

    /**
     * Parse the cookie value into a Map of productId -> quantity
     * Format: "productId:quantity_productId:quantity"
     */
    public static Map<Integer, Integer> getCartMap(Cookie[] cookies) {
        Map<Integer, Integer> cartMap = new HashMap<>();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (COOKIE_NAME.equals(cookie.getName())) {
                    String value = cookie.getValue();
                    if (value != null && !value.isEmpty()) {
                        String[] items = value.split("_");
                        for (String item : items) {
                            String[] parts = item.split(":");
                            if (parts.length == 2) {
                                try {
                                    int productId = Integer.parseInt(parts[0]);
                                    int quantity = Integer.parseInt(parts[1]);
                                    cartMap.put(productId, quantity);
                                } catch (NumberFormatException ignored) {}
                            }
                        }
                    }
                    break;
                }
            }
        }
        return cartMap;
    }

    /**
     * Serialize the Map to a cookie string and add it to the response
     */
    public static void saveCartCookie(Map<Integer, Integer> cartMap, HttpServletResponse response) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Integer> entry : cartMap.entrySet()) {
            if (sb.length() > 0) {
                sb.append("_");
            }
            sb.append(entry.getKey()).append(":").append(entry.getValue());
        }

        Cookie cookie = new Cookie(COOKIE_NAME, sb.toString());
        cookie.setMaxAge(MAX_AGE);
        cookie.setPath("/"); // Accessible from everywhere
        response.addCookie(cookie);
    }

    /**
     * Xoa gio hang: ghi de cookie voi gia tri rong va maxAge = 0.
     * Goi sau khi dat hang thanh cong.
     */
    public static void clearCartCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * Calculate total items count directly from the cookies array
     */
    public static int getCartCount(Cookie[] cookies) {
        int count = 0;
        Map<Integer, Integer> cartMap = getCartMap(cookies);
        for (int qty : cartMap.values()) {
            count += qty;
        }
        return count;
    }
}
