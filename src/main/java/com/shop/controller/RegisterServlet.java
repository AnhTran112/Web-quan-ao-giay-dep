package com.shop.controller;

import com.shop.dao.UserDAO;
import com.shop.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String fullName = req.getParameter("fullName");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");

        if (!password.equals(confirmPassword)) {
            req.setAttribute("error", "Mật khẩu xác nhận không khớp!");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }

        if (userDAO.findByUsername(username) != null) {
            req.setAttribute("error", "Tên đăng nhập đã tồn tại!");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setFullName(fullName);
        newUser.setPhone(phone);
        newUser.setAddress(address);
        newUser.setRole("CUSTOMER");

        if (userDAO.insertUser(newUser)) {
            resp.sendRedirect(req.getContextPath() + "/login?msg=RegisteredSuccessfully");
        } else {
            req.setAttribute("error", "Đăng ký thất bại, vui lòng thử lại!");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
        }
    }
}
