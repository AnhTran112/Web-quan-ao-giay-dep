package com.shop.controller;

import com.shop.dao.ProductDAO;
import com.shop.model.CartItem;
import com.shop.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Quan ly gio hang bang Session. URL: "/cart"
 */
@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("remove".equals(action)) {
            handleRemove(req);
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        
        if ("add".equals(action)) {
            handleAdd(req);
        } else if ("update".equals(action)) {
            handleUpdate(req);
        } else if ("remove".equals(action)) {
            handleRemove(req);
        }

        resp.sendRedirect(req.getContextPath() + "/cart");
    }

    private void handleAdd(HttpServletRequest req) {
        int productId = Integer.parseInt(req.getParameter("productId"));
        int quantity = Integer.parseInt(req.getParameter("quantity"));

        HttpSession session = req.getSession();
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

        // Check if item already exists
        for (CartItem item : cart) {
            if (item.getProductId() == productId) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }

        // Fetch product and add as new CartItem
        Product product = productDAO.getById(productId);
        if (product != null) {
            cart.add(new CartItem(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImage(),
                quantity
            ));
        }
    }

    private void handleUpdate(HttpServletRequest req) {
        int productId = Integer.parseInt(req.getParameter("productId"));
        int quantity = Integer.parseInt(req.getParameter("quantity"));

        HttpSession session = req.getSession();
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart != null) {
            for (CartItem item : cart) {
                if (item.getProductId() == productId) {
                    if (quantity > 0) {
                        item.setQuantity(quantity);
                    } else {
                        cart.remove(item);
                    }
                    break;
                }
            }
        }
    }

    private void handleRemove(HttpServletRequest req) {
        int productId = Integer.parseInt(req.getParameter("productId"));

        HttpSession session = req.getSession();
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart != null) {
            cart.removeIf(item -> item.getProductId() == productId);
        }
    }
}
