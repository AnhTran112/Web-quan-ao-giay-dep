package com.shop.controller.admin;

import com.shop.dao.UserDAO;
import com.shop.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

import java.util.UUID;
import java.sql.Timestamp;

/**
 * Dang nhap / dang xuat cho ca Admin va Khach. URL: "/login", "/logout", "/admin/login", "/admin/logout"
 */
@WebServlet({"/login", "/logout", "/admin/login", "/admin/logout"})
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION = 15 * 60 * 1000; // 15 phut

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        
        // Dang xuat
        if (path.endsWith("/logout")) {
            req.getSession().invalidate();
            // Xoa cookie remember me
            Cookie cookie = new Cookie("remember_me", "");
            cookie.setMaxAge(0);
            cookie.setPath("/");
            resp.addCookie(cookie);
            
            resp.sendRedirect(req.getContextPath() + "/login");
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
        String remember = req.getParameter("remember");

        // Thieu tham so -> bao loi thay vi de BCrypt.checkpw nem NullPointerException
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập tài khoản và mật khẩu!");
            req.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(req, resp);
            return;
        }

        User user = userDAO.findByUsername(username);

        if (user != null) {
            // Check lock status
            if ("LOCKED".equals(user.getStatus())) {
                req.setAttribute("error", "Tài khoản của bạn đã bị vô hiệu hóa.");
                req.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(req, resp);
                return;
            }

            // Check brute-force lock
            if (user.getLockTime() != null) {
                long lockTimeInMillis = user.getLockTime().getTime();
                long currentTimeInMillis = System.currentTimeMillis();
                
                if (currentTimeInMillis - lockTimeInMillis < LOCK_TIME_DURATION) {
                    long remainingTime = (LOCK_TIME_DURATION - (currentTimeInMillis - lockTimeInMillis)) / 1000 / 60;
                    req.setAttribute("error", "Tài khoản bị khóa tạm thời. Vui lòng thử lại sau " + (remainingTime + 1) + " phút.");
                    req.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(req, resp);
                    return;
                } else {
                    // Unlock account
                    userDAO.updateFailedAttempts(username, 0, null);
                    user.setFailedAttempts(0);
                }
            }

            // Kiểm tra mật khẩu bằng thuật toán BCrypt
            if (org.mindrot.jbcrypt.BCrypt.checkpw(password, user.getPassword())) {
                // Reset failed attempts
                userDAO.updateFailedAttempts(username, 0, null);
                
                req.getSession().setAttribute("loggedInUser", user);
                // Ho tro code cu
                req.getSession().setAttribute("admin", user);

                // Remember me
                if ("on".equals(remember)) {
                    String token = UUID.randomUUID().toString();
                    userDAO.updateRememberToken(user.getId(), token);
                    Cookie cookie = new Cookie("remember_me", token);
                    cookie.setMaxAge(30 * 24 * 60 * 60); // 30 ngay
                    cookie.setPath("/");
                    resp.addCookie(cookie);
                }

                // Chuyen trang theo role
                if ("CUSTOMER".equals(user.getRole())) {
                    resp.sendRedirect(req.getContextPath() + "/");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
                }
                return;
            } else {
                // Tang so lan nhap sai
                int attempts = user.getFailedAttempts() + 1;
                Timestamp lockTime = null;
                if (attempts >= MAX_FAILED_ATTEMPTS) {
                    lockTime = new Timestamp(System.currentTimeMillis());
                }
                userDAO.updateFailedAttempts(username, attempts, lockTime);
            }
        }

        req.setAttribute("error", "Sai tài khoản hoặc mật khẩu!");
        req.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(req, resp);
    }
}
