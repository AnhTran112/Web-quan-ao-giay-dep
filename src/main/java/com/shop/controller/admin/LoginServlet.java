package com.shop.controller.admin;

import com.shop.dao.UserDAO;
import com.shop.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Dang nhap / dang xuat admin. URL: "/admin/login", "/admin/logout"
 */
@WebServlet({"/admin/login", "/admin/logout"})
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Dang xuat
        if (req.getServletPath().equals("/admin/logout")) {
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/admin/login");
            return;
        }
        // Hien thi form login
        req.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        User user = userDAO.findByUsername(username);
        // Kiểm tra mật khẩu bằng thuật toán BCrypt
        if (user != null && org.mindrot.jbcrypt.BCrypt.checkpw(password, user.getPassword())) {
            req.getSession().setAttribute("admin", user);
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        } else {
            req.setAttribute("error", "Sai tai khoan hoac mat khau!");
            req.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(req, resp);
        }
    }
}
