package com.shop.controller.admin;

import com.shop.dao.OrderDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Trang thong ke admin. URL: "/admin/dashboard"
 *
 * NGUOI PHU TRACH: Nguoi 4 (Nguyen).
 * Servlet nay chi GOI getTotalRevenue()/countOrders() (do Nguoi 3 - Khoa viet trong OrderDAO).
 * TODO (Nguyen, nang cap): them bieu do Chart.js, top san pham ban chay, canh bao het hang.
 */
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
