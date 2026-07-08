package com.shop.controller;

import com.shop.dao.ProductDAO;
import com.shop.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/wishlist")
public class WishlistServlet extends HttpServlet {
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Product> wishlist = new ArrayList<>();
        String val = "";
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("wishlist".equals(c.getName())) {
                    try { val = URLDecoder.decode(c.getValue(), "UTF-8"); } catch (Exception ignored) {}
                    break;
                }
            }
        }
        
        String[] parts = val.split(",");
        for (String pId : parts) {
            if (!pId.trim().isEmpty()) {
                Product p = productDAO.getById(Integer.parseInt(pId.trim()));
                if (p != null) wishlist.add(p);
            }
        }
        
        req.setAttribute("wishlist", wishlist);
        req.getRequestDispatcher("/WEB-INF/views/wishlist.jsp").forward(req, resp);
    }
}
