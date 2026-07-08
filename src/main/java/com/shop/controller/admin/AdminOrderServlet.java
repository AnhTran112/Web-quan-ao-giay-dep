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
 *   doGet  : lay tat ca don hang, hien thi order-list.jsp.
 *   doPost : doi trang thai don hang thanh "DELIVERED".
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
        // Doc id don hang tu form
        int id = Integer.parseInt(req.getParameter("id"));
        // Doi trang thai thanh "Da giao"
        orderDAO.updateStatus(id, "DELIVERED");
        // Quay lai danh sach don hang
        resp.sendRedirect(req.getContextPath() + "/admin/orders");
    }
}
