package com.shop.controller;

import com.shop.dao.ProductDAO;
import com.shop.model.CartItem;
import com.shop.model.Product;
import com.shop.util.CartUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Quan ly gio hang bang Cookie. URL: "/cart"
 */
@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("remove".equals(action)) {
            handleRemove(req, resp);
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        // Fetch products and prepare cartList for the view
        Map<Integer, Integer> cartMap = CartUtil.getCartMap(req.getCookies());
        List<CartItem> cartList = new ArrayList<>();
        
        for (Map.Entry<Integer, Integer> entry : cartMap.entrySet()) {
            int productId = entry.getKey();
            int quantity = entry.getValue();
            
            Product product = productDAO.getById(productId);
            if (product != null) {
                cartList.add(new CartItem(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getImage(),
                        quantity
                ));
            }
        }
        
        req.setAttribute("cartList", cartList);
        req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        
        if ("add".equals(action)) {
            handleAdd(req, resp);
        } else if ("update".equals(action)) {
            handleUpdate(req, resp);
        } else if ("remove".equals(action)) {
            handleRemove(req, resp);
        }

        resp.sendRedirect(req.getContextPath() + "/cart");
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp) {
        int productId = Integer.parseInt(req.getParameter("productId"));
        int quantity = Integer.parseInt(req.getParameter("quantity"));

        Map<Integer, Integer> cartMap = CartUtil.getCartMap(req.getCookies());
        
        // Update quantity if exists, else add new
        int currentQty = cartMap.getOrDefault(productId, 0);
        cartMap.put(productId, currentQty + quantity);
        
        CartUtil.saveCartCookie(cartMap, resp);
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) {
        int productId = Integer.parseInt(req.getParameter("productId"));
        int quantity = Integer.parseInt(req.getParameter("quantity"));

        Map<Integer, Integer> cartMap = CartUtil.getCartMap(req.getCookies());
        
        if (quantity > 0) {
            cartMap.put(productId, quantity);
        } else {
            cartMap.remove(productId);
        }
        
        CartUtil.saveCartCookie(cartMap, resp);
    }

    private void handleRemove(HttpServletRequest req, HttpServletResponse resp) {
        int productId = Integer.parseInt(req.getParameter("productId"));

        Map<Integer, Integer> cartMap = CartUtil.getCartMap(req.getCookies());
        cartMap.remove(productId);
        
        CartUtil.saveCartCookie(cartMap, resp);
    }
}
