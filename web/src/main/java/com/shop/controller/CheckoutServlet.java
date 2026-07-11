package com.shop.controller;

import com.shop.dao.CouponDAO;
import com.shop.dao.OrderDAO;
import com.shop.model.CartItem;
import com.shop.model.Coupon;
import com.shop.model.Order;
import com.shop.model.OrderItem;
import com.shop.util.CartCookieHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Dat hang (KHONG thanh toan online - gui don cho admin lien he). URL: "/checkout"
 *
 * NGUOI PHU TRACH: Nguoi 3 (Khoa).
 *
 * Luong:
 *   1. doGet  : doc gio tu Cookie, ap ma giam gia (neu co), tinh phi ship, hien checkout.jsp.
 *               ?coupon=MA      -> kiem tra va ap ma (luu vao session).
 *               ?removeCoupon=1 -> bo ma dang ap.
 *   2. doPost : VALIDATE du lieu phia server -> tao Order (kem tru kho, tru luot coupon
 *               trong transaction cua OrderDAO) -> xoa gio + ma -> trang thanh cong.
 *
 * Phi van chuyen: 30.000d, MIEN PHI cho don co tam tinh tu 500.000d.
 */
@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private static final BigDecimal SHIP_FEE = BigDecimal.valueOf(30000);
    private static final BigDecimal FREE_SHIP_THRESHOLD = BigDecimal.valueOf(500000);
    private static final String SESSION_COUPON = "checkoutCoupon";

    /** SDT di dong VN: 10 so, bat dau 03/05/07/08/09. */
    private static final String PHONE_PATTERN = "^0[35789]\\d{8}$";

    private final OrderDAO orderDAO = new OrderDAO();
    private final CouponDAO couponDAO = new CouponDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        List<CartItem> cart = CartCookieHelper.getCart(req);
        if (cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        HttpSession session = req.getSession();

        // Bo ma giam gia dang ap
        if ("1".equals(req.getParameter("removeCoupon"))) {
            session.removeAttribute(SESSION_COUPON);
            resp.sendRedirect(req.getContextPath() + "/checkout");
            return;
        }

        // Khach nhap ma giam gia
        String couponInput = req.getParameter("coupon");
        if (couponInput != null) {
            couponInput = couponInput.trim().toUpperCase();
            if (couponInput.isEmpty()) {
                req.setAttribute("couponError", "Vui lòng nhập mã giảm giá.");
            } else if (couponDAO.getValidByCode(couponInput) != null) {
                session.setAttribute(SESSION_COUPON, couponInput);
                req.setAttribute("couponSuccess", "Đã áp dụng mã \"" + couponInput + "\".");
            } else {
                req.setAttribute("couponError",
                        "Mã \"" + couponInput + "\" không tồn tại hoặc đã hết hiệu lực.");
            }
        }

        render(req, resp, cart);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        req.setCharacterEncoding("UTF-8");

        List<CartItem> cart = CartCookieHelper.getCart(req);
        if (cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        String customerName = trim(req.getParameter("customerName"));
        String phone        = trim(req.getParameter("phone"));
        String address      = trim(req.getParameter("address"));
        String note         = trim(req.getParameter("note"));

        // ===== VALIDATE PHIA SERVER (khong tin form HTML) =====
        List<String> errors = new ArrayList<>();
        if (customerName.length() < 2 || customerName.length() > 150) {
            errors.add("Họ tên phải từ 2 đến 150 ký tự.");
        }
        if (!phone.matches(PHONE_PATTERN)) {
            errors.add("Số điện thoại không hợp lệ (10 số, bắt đầu bằng 03/05/07/08/09).");
        }
        if (address.length() < 5 || address.length() > 255) {
            errors.add("Địa chỉ phải từ 5 đến 255 ký tự.");
        }
        if (note.length() > 500) {
            errors.add("Ghi chú tối đa 500 ký tự.");
        }
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            render(req, resp, cart); // form giu lai gia tri da nhap qua ${param...}
            return;
        }

        // ===== TINH TIEN PHIA SERVER (gia doc tu DB qua CartCookieHelper) =====
        Totals totals = computeTotals(req, cart);

        com.shop.model.User loggedUser = (com.shop.model.User) req.getSession().getAttribute("loggedInUser");

        Order order = new Order();
        if (loggedUser != null) {
            order.setUserId(loggedUser.getId());
        }
        order.setCustomerName(customerName);
        order.setPhone(phone);
        order.setAddress(address);
        order.setNote(note.isEmpty() ? null : note);
        order.setTotalAmount(totals.grandTotal);
        order.setShipFee(totals.shipFee);
        order.setCouponCode(totals.couponCode);
        order.setDiscountAmount(totals.discount);

        // Chuyen CartItem -> OrderItem (luu gia + phan loai tai thoi diem mua)
        List<OrderItem> items = new ArrayList<>();
        for (CartItem cartItem : cart) {
            OrderItem oi = new OrderItem();
            oi.setProductId(cartItem.getProductId());
            oi.setVariantId(cartItem.getVariantId());
            oi.setVariantName(cartItem.getVariantName());
            oi.setProductName(cartItem.getName()); // de bao loi het hang ro rang
            oi.setQuantity(cartItem.getQuantity());
            oi.setPrice(cartItem.getPrice());
            items.add(oi);
        }
        order.setItems(items);

        try {
            int orderId = orderDAO.createOrder(order);

            // Thanh cong -> xoa gio + ma giam gia
            CartCookieHelper.clearCart(resp);
            req.getSession().removeAttribute(SESSION_COUPON);

            req.setAttribute("orderId", orderId);
            req.setAttribute("orderTotal", totals.grandTotal);
            req.setAttribute("customerPhone", phone);
            req.getRequestDispatcher("/WEB-INF/views/order-success.jsp").forward(req, resp);

        } catch (OrderDAO.OrderException e) {
            // Het hang / ma het hieu luc / loi he thong -> quay lai voi thong bao ro rang
            req.setAttribute("orderError", e.getMessage());
            render(req, resp, cart);
        }
    }

    // ========================= HELPER =========================

    /** Tinh tam tinh / giam gia / phi ship / tong cuoi va day vao request de JSP hien thi. */
    private void render(HttpServletRequest req, HttpServletResponse resp, List<CartItem> cart)
            throws ServletException, IOException {
        Totals totals = computeTotals(req, cart);
        req.setAttribute("cartItems", cart);
        req.setAttribute("subtotal", totals.subtotal);
        req.setAttribute("discount", totals.discount);
        req.setAttribute("appliedCoupon", totals.couponCode);
        req.setAttribute("couponPercent", totals.couponPercent);
        req.setAttribute("shipFee", totals.shipFee);
        req.setAttribute("grandTotal", totals.grandTotal);
        req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
    }

    private Totals computeTotals(HttpServletRequest req, List<CartItem> cart) {
        Totals t = new Totals();

        t.subtotal = BigDecimal.ZERO;
        for (CartItem item : cart) {
            t.subtotal = t.subtotal.add(item.getSubtotal());
        }

        // Ma giam gia trong session: KIEM TRA LAI moi lan tinh (co the vua het luot/het han)
        HttpSession session = req.getSession();
        String code = (String) session.getAttribute(SESSION_COUPON);
        if (code != null) {
            Coupon coupon = couponDAO.getValidByCode(code);
            if (coupon != null) {
                t.couponCode = coupon.getCode();
                t.couponPercent = coupon.getDiscountPercent();
                t.discount = t.subtotal.multiply(BigDecimal.valueOf(coupon.getDiscountPercent()))
                        .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
            } else {
                session.removeAttribute(SESSION_COUPON);
                req.setAttribute("couponError", "Mã \"" + code + "\" vừa hết hiệu lực nên đã được bỏ.");
            }
        }

        t.shipFee = t.subtotal.compareTo(FREE_SHIP_THRESHOLD) >= 0 ? BigDecimal.ZERO : SHIP_FEE;
        t.grandTotal = t.subtotal.subtract(t.discount).add(t.shipFee);
        return t;
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }

    /** Gom cac con so tien de truyen giua cac ham. */
    private static class Totals {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal shipFee = BigDecimal.ZERO;
        BigDecimal grandTotal = BigDecimal.ZERO;
        String couponCode = null;
        int couponPercent = 0;
    }
}
