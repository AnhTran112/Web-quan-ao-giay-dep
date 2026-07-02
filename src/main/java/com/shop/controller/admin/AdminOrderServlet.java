package com.shop.controller.admin;

import com.shop.dao.OrderDAO;
import com.shop.model.Order;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Quan ly don hang (admin). URL: "/admin/orders"
 *
 * NGUOI PHU TRACH: Nguoi 3 (Khoa).
 *
 * Chuc nang:
 *  - doGet: hien thi danh sach don hang HOAC chi tiet 1 don hang.
 *    + /admin/orders              -> danh sach tat ca don hang
 *    + /admin/orders?action=detail&id=...  -> chi tiet 1 don hang (Nang cap)
 *  - doPost: doi trang thai don hang tu PENDING sang DELIVERED.
 */
@WebServlet("/admin/orders")
public class AdminOrderServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");

        if ("detail".equals(action)) {
            // ====== (NANG CAP) Xem chi tiet 1 don hang ======
            int id = Integer.parseInt(req.getParameter("id"));
            Order order = orderDAO.getById(id);
            if (order == null) {
                // Don hang khong ton tai -> quay ve danh sach
                resp.sendRedirect(req.getContextPath() + "/admin/orders");
                return;
            }
            req.setAttribute("order", order);
            req.getRequestDispatcher("/WEB-INF/views/admin/order-detail.jsp").forward(req, resp);
        } else {
            // ====== Danh sach tat ca don hang ======
            req.setAttribute("orders", orderDAO.getAll());
            req.getRequestDispatcher("/WEB-INF/views/admin/order-list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Doc id don hang tu form va doi trang thai sang DELIVERED
        int id = Integer.parseInt(req.getParameter("id"));
        orderDAO.updateStatus(id, "DELIVERED");
        resp.sendRedirect(req.getContextPath() + "/admin/orders");
    }
}
