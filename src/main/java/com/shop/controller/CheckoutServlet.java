package com.shop.controller;

import com.shop.dao.OrderDAO;
import com.shop.model.CartItem;
import com.shop.model.Order;
import com.shop.model.OrderItem;
import com.shop.util.CartCookieHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Dat hang (KHONG thanh toan online - chi gui form cho admin lien he). URL: "/checkout"
 *
 * NGUOI PHU TRACH: Nguoi 3 (Khoa).
 *
 * Luong:
 *   1. doGet  : doc gio hang tu Cookie, tinh tong tien, hien thi checkout.jsp.
 *   2. doPost : doc thong tin khach, tao Order tu gio hang, luu DB, xoa gio, bao thanh cong.
 */
@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        // Doc gio hang tu cookie
        List<CartItem> cart = CartCookieHelper.getCart(req);

        // Gio trong -> quay ve trang gio hang
        if (cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        // Tinh tong tien
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cart) {
            total = total.add(item.getSubtotal());
        }

        req.setAttribute("cartItems", cart);
        req.setAttribute("cartTotal", total);
        req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        req.setCharacterEncoding("UTF-8");

        // Doc thong tin khach hang tu form
        String customerName = req.getParameter("customerName");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");

        // Doc gio hang tu cookie
        List<CartItem> cart = CartCookieHelper.getCart(req);

        // Gio trong -> quay ve
        if (cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        // Tinh tong tien
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cart) {
            total = total.add(item.getSubtotal());
        }

        // Tao doi tuong Order
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setPhone(phone);
        order.setAddress(address);
        order.setTotalAmount(total);

        // Chuyen CartItem thanh OrderItem (luu gia tai thoi diem mua)
        List<OrderItem> items = new ArrayList<>();
        for (CartItem cartItem : cart) {
            OrderItem oi = new OrderItem();
            oi.setProductId(cartItem.getProductId());
            oi.setQuantity(cartItem.getQuantity());
            oi.setPrice(cartItem.getPrice()); // gia tai thoi diem mua
            items.add(oi);
        }
        order.setItems(items);

        // Luu don hang vao DB (co transaction)
        int orderId = orderDAO.createOrder(order);

        if (orderId > 0) {
            // Thanh cong -> xoa gio hang khoi cookie
            CartCookieHelper.clearCart(resp);
            req.setAttribute("orderId", orderId);
            req.getRequestDispatcher("/WEB-INF/views/order-success.jsp").forward(req, resp);
        } else {
            // That bai -> quay lai trang checkout voi thong bao loi
            req.setAttribute("error", "Đặt hàng thất bại. Vui lòng thử lại.");
            doGet(req, resp);
        }
    }
}
