package com.shop.controller.admin;

import com.shop.dao.OrderDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Quan ly don hang (admin). URL: "/admin/orders"
 *
 * NGUOI PHU TRACH: Nguoi 3 (Khoa).
 * doGet da goi orderDAO.getAll() (chay duoc khi Khoa hoan thien OrderDAO).
 * TODO: doPost doi trang thai don -> "Da giao".
 */
@WebServlet("/admin/orders")
public class AdminOrderServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("orders", orderDAO.getAll());
        req.getRequestDispatcher("/WEB-INF/views/admin/order-list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // TODO (Khoa): int id = ...; orderDAO.updateStatus(id, "DELIVERED");
        resp.sendRedirect(req.getContextPath() + "/admin/orders");
    }
}
