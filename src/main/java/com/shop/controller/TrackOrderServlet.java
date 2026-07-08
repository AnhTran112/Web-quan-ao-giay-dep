package com.shop.controller;

import com.shop.dao.OrderDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Tra cuu don hang bang so dien thoai (danh cho khach, khong can tai khoan).
 * URL: "/track-order"
 *
 * NGUOI PHU TRACH: Nguoi 3 (Khoa).
 *
 * doGet:
 *   - Khong co ?phone -> hien form nhap SDT.
 *   - Co ?phone      -> validate dinh dang, tim don theo SDT (kem danh sach mon),
 *                       hien ket qua ngay duoi form.
 */
@WebServlet("/track-order")
public class TrackOrderServlet extends HttpServlet {

    /** SDT di dong VN: 10 so, bat dau 03/05/07/08/09 (giong CheckoutServlet). */
    private static final String PHONE_PATTERN = "^0[35789]\\d{8}$";

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String phone = req.getParameter("phone");
        if (phone != null) {
            phone = phone.trim();
            if (!phone.matches(PHONE_PATTERN)) {
                req.setAttribute("error", "Số điện thoại không hợp lệ (10 số, bắt đầu bằng 03/05/07/08/09).");
            } else {
                req.setAttribute("orders", orderDAO.getByPhone(phone));
                req.setAttribute("searched", true);
            }
            req.setAttribute("phone", phone);
        }

        req.getRequestDispatcher("/WEB-INF/views/order-lookup.jsp").forward(req, resp);
    }
}
