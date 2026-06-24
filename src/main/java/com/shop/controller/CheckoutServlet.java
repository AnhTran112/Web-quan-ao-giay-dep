package com.shop.controller;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Dat hang (KHONG thanh toan online - chi gui form cho admin lien he). URL: "/checkout"
 *
 * NGUOI PHU TRACH: Nguoi 3 (Khoa).
 * TODO:
 *  - doGet : tinh tong tien tu gio hang (session "cart") roi hien thi checkout.jsp.
 *  - doPost: doc customerName, phone, address; tao Order tu gio hang;
 *            goi OrderDAO.createOrder(order); XOA gio hang khoi session;
 *            forward sang order-success.jsp.
 *  - (Nang cap) tru ton kho + chan dat khi het hang.
 */
@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, javax.servlet.ServletException {
        // TODO (Khoa): tinh tong tien tu gio hang truoc khi hien thi
        req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, javax.servlet.ServletException {
        // TODO (Khoa): tao don hang that su tu gio hang + luu DB + xoa gio
        req.getRequestDispatcher("/WEB-INF/views/order-success.jsp").forward(req, resp);
    }
}
