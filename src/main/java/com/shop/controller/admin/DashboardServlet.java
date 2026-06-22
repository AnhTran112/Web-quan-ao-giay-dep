package com.shop.controller.admin;

import com.shop.dao.OrderDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/** Trang thong ke admin. URL: "/admin/dashboard" */
@WebServlet("/admin/dashboard")
public class DashboardServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("totalRevenue", orderDAO.getTotalRevenue());
        req.setAttribute("totalOrders", orderDAO.countOrders());
        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }
}
