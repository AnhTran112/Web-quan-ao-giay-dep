package com.shop.controller;

import com.shop.dao.WishlistDAO;
import com.shop.model.Product;
import com.shop.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/wishlist")
public class WishlistServlet extends HttpServlet {
    private final WishlistDAO wishlistDAO = new WishlistDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login?error="
                    + java.net.URLEncoder.encode("Lỗi: Vui lòng đăng nhập để xem danh sách yêu thích", "UTF-8"));
            return;
        }

        User user = (User) session.getAttribute("loggedInUser");
        List<Product> wishlist = wishlistDAO.getByUserId(user.getId());

        req.setAttribute("wishlist", wishlist);
        req.getRequestDispatcher("/WEB-INF/views/wishlist.jsp").forward(req, resp);
    }
}
