package com.shop.controller;

import com.shop.dao.ProductDAO;
import com.shop.model.CartItem;
import com.shop.model.Product;
import com.shop.util.CartCookieHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Quan ly gio hang bang Cookie (thay vi Session). URL: "/cart"
 *
 * Cach hoat dong:
 *   - Doc gio hang tu cookie bang CartCookieHelper.getCart(req).
 *   - Xu ly action (add / update / remove).
 *   - Ghi lai gio hang vao cookie bang CartCookieHelper.saveCart(resp, cart).
 *   - Set gio hang vao request attribute de JSP doc.
 *
 * Cac action:
 *   - action=add    : them san pham vao gio (tu form o product-detail.jsp)
 *   - action=update : doi so luong 1 dong theo productId
 *   - action=remove : xoa 1 dong theo productId
 */
@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        List<CartItem> cart = CartCookieHelper.getCart(req);
        String action = req.getParameter("action");

        if ("remove".equals(action)) {
            int productId = Integer.parseInt(req.getParameter("productId"));
            cart.removeIf(item -> item.getProductId() == productId);
            CartCookieHelper.saveCart(resp, cart);
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        if ("update".equals(action)) {
            int productId = Integer.parseInt(req.getParameter("productId"));
            int quantity = Integer.parseInt(req.getParameter("quantity"));
            for (CartItem item : cart) {
                if (item.getProductId() == productId) {
                    if (quantity <= 0) {
                        cart.remove(item);
                    } else {
                        item.setQuantity(quantity);
                    }
                    break;
                }
            }
            CartCookieHelper.saveCart(resp, cart);
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        // Mac dinh: hien thi gio hang
        // Tinh tong tien
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cart) {
            total = total.add(item.getSubtotal());
        }
        req.setAttribute("cartItems", cart);
        req.setAttribute("cartTotal", total);
        req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        String action = req.getParameter("action");

        if ("add".equals(action)) {
            int productId = Integer.parseInt(req.getParameter("productId"));
            int quantity = 1;
            try { quantity = Integer.parseInt(req.getParameter("quantity")); }
            catch (Exception ignored) {}

            // Lay thong tin san pham tu DB
            Product product = productDAO.getById(productId);
            if (product == null) {
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }

            // Tinh gia ban (ap dung giam gia neu co)
            BigDecimal price = product.getPrice();
            if (product.getDiscountPercent() > 0) {
                price = price.multiply(BigDecimal.valueOf(100 - product.getDiscountPercent()))
                             .divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
            }

            // Doc gio hang hien tai tu cookie
            List<CartItem> cart = CartCookieHelper.getCart(req);

            // Kiem tra san pham da co trong gio chua
            boolean found = false;
            for (CartItem item : cart) {
                if (item.getProductId() == productId) {
                    item.setQuantity(item.getQuantity() + quantity); // cong don
                    found = true;
                    break;
                }
            }

            // Chua co -> them moi
            if (!found) {
                CartItem newItem = new CartItem(
                        productId,
                        product.getName(),
                        price,
                        product.getImage(),
                        quantity
                );
                cart.add(newItem);
            }

            // Luu lai vao cookie
            CartCookieHelper.saveCart(resp, cart);

            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        // Action khac -> xu ly nhu GET
        doGet(req, resp);
    }
}
