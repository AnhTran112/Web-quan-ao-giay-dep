package com.shop.controller.api;

import com.shop.dao.WishlistDAO;
import com.shop.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet({"/api/wishlist/toggle", "/api/wishlist/count"})
public class WishlistApiServlet extends HttpServlet {
    private final WishlistDAO wishlistDAO = new WishlistDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/api/wishlist/count".equals(path)) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter out = resp.getWriter();

            HttpSession session = req.getSession(false);
            if (session != null && session.getAttribute("loggedInUser") != null) {
                User user = (User) session.getAttribute("loggedInUser");
                int count = wishlistDAO.getWishlistCount(user.getId());
                out.print("{\"count\": " + count + "}");
            } else {
                out.print("{\"count\": 0}");
            }
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/api/wishlist/toggle".equals(path)) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter out = resp.getWriter();

            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("loggedInUser") == null) {
                out.print("{\"success\": false, \"error\": \"not_logged_in\"}");
                out.flush();
                return;
            }

            User user = (User) session.getAttribute("loggedInUser");
            int productId;
            try {
                productId = Integer.parseInt(req.getParameter("productId"));
            } catch (Exception e) {
                out.print("{\"success\": false, \"message\": \"Invalid product ID\"}");
                out.flush();
                return;
            }

            boolean isFav = wishlistDAO.isFavorite(user.getId(), productId);
            if (isFav) {
                wishlistDAO.remove(user.getId(), productId);
                out.print("{\"success\": true, \"action\": \"removed\"}");
            } else {
                wishlistDAO.add(user.getId(), productId);
                out.print("{\"success\": true, \"action\": \"added\"}");
            }
            out.flush();
        }
    }
}
