package com.shop.controller;

import com.shop.dao.OrderDAO;
import com.shop.dao.UserDAO;
import com.shop.model.Order;
import com.shop.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/account")
public class AccountServlet extends HttpServlet {
    private final OrderDAO orderDAO = new OrderDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User loggedUser = (User) req.getSession().getAttribute("loggedInUser");
        if (loggedUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login?msg=PleaseLoginToViewAccount");
            return;
        }

        // Fetch user's orders
        List<Order> orders = orderDAO.getByUserId(loggedUser.getId());
        req.setAttribute("orders", orders);

        req.getRequestDispatcher("/WEB-INF/views/account.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User loggedUser = (User) req.getSession().getAttribute("loggedInUser");
        if (loggedUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String fullName = req.getParameter("fullName");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");

        if (userDAO.updateProfile(loggedUser.getId(), fullName, phone, address)) {
            // Update session
            loggedUser.setFullName(fullName);
            loggedUser.setPhone(phone);
            loggedUser.setAddress(address);
            req.getSession().setAttribute("loggedInUser", loggedUser);
            resp.sendRedirect(req.getContextPath() + "/account?tab=profile&success=ProfileUpdated");
        } else {
            req.setAttribute("error", "Cập nhật thông tin thất bại!");
            
            // Refetch orders to display on account.jsp since we are forwarding
            List<Order> orders = orderDAO.getByUserId(loggedUser.getId());
            req.setAttribute("orders", orders);
            req.getRequestDispatcher("/WEB-INF/views/account.jsp").forward(req, resp);
        }
    }
}
