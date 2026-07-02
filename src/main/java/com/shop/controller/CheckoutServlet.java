package com.shop.controller;

import com.shop.dao.OrderDAO;
import com.shop.dao.ProductDAO;
import com.shop.model.CartItem;
import com.shop.model.Order;
import com.shop.model.OrderItem;
import com.shop.model.Product;
import com.shop.util.CartUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Dat hang (KHONG thanh toan online - chi gui form cho admin lien he). URL: "/checkout"
 *
 * NGUOI PHU TRACH: Nguoi 3 (Khoa).
 *
 * Luong xu ly:
 *  - doGet : doc gio hang tu cookie, truy van DB lay thong tin san pham,
 *            tinh tong tien roi hien thi checkout.jsp.
 *  - doPost: doc customerName, phone, address; kiem tra ton kho;
 *            tao Order tu gio hang; goi OrderDAO.createOrder(order);
 *            xoa cookie gio hang; forward sang order-success.jsp.
 */
@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();
    private final ProductDAO productDAO = new ProductDAO();

    /**
     * Doc gio hang tu cookie, chuyen thanh danh sach CartItem.
     * Moi dong trong cookie chi luu productId:quantity,
     * nen phai truy van DB de lay ten, gia, anh san pham.
     */
    private List<CartItem> buildCartList(HttpServletRequest req) {
        Map<Integer, Integer> cartMap = CartUtil.getCartMap(req.getCookies());
        List<CartItem> cartList = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : cartMap.entrySet()) {
            int productId = entry.getKey();
            int quantity  = entry.getValue();

            Product product = productDAO.getById(productId);
            if (product != null) {
                cartList.add(new CartItem(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getImage(),
                        quantity
                ));
            }
        }
        return cartList;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        // Doc gio hang tu cookie
        List<CartItem> cartList = buildCartList(req);

        // Neu gio hang rong -> chuyen ve trang gio hang
        if (cartList.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        // Tinh tong tien cua gio hang de hien thi tren trang checkout
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : cartList) {
            totalAmount = totalAmount.add(item.getSubtotal());
        }

        req.setAttribute("cartList", cartList);
        req.setAttribute("totalAmount", totalAmount);
        req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        req.setCharacterEncoding("UTF-8");

        // --- 1. Doc gio hang tu cookie ---
        List<CartItem> cartList = buildCartList(req);

        if (cartList.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        // --- 2. Doc thong tin khach hang tu form ---
        String customerName = req.getParameter("customerName");
        String phone        = req.getParameter("phone");
        String address      = req.getParameter("address");

        // --- 3. (Nang cap) Kiem tra ton kho truoc khi dat hang ---
        List<String> outOfStockItems = new ArrayList<>();
        for (CartItem ci : cartList) {
            Product product = productDAO.getById(ci.getProductId());
            if (product == null) {
                outOfStockItems.add(ci.getName() + " (sản phẩm không tồn tại)");
            } else if (product.getQuantity() < ci.getQuantity()) {
                outOfStockItems.add(ci.getName() + " (chỉ còn " + product.getQuantity() + " sản phẩm)");
            }
        }

        if (!outOfStockItems.isEmpty()) {
            // Khong du ton kho -> bao loi, quay lai trang checkout
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (CartItem item : cartList) {
                totalAmount = totalAmount.add(item.getSubtotal());
            }
            req.setAttribute("cartList", cartList);
            req.setAttribute("totalAmount", totalAmount);
            req.setAttribute("errorMessage",
                    "Không thể đặt hàng. Các sản phẩm sau không đủ tồn kho: "
                    + String.join(", ", outOfStockItems));
            // Giu lai thong tin khach da nhap de khong phai nhap lai
            req.setAttribute("customerName", customerName);
            req.setAttribute("phone", phone);
            req.setAttribute("address", address);
            req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
            return;
        }

        // --- 4. Chuyen CartItem thanh OrderItem va tinh tong tien ---
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem ci : cartList) {
            OrderItem oi = new OrderItem();
            oi.setProductId(ci.getProductId());
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(ci.getPrice()); // Luu gia tai thoi diem mua
            orderItems.add(oi);
            totalAmount = totalAmount.add(ci.getSubtotal());
        }

        // --- 5. Tao doi tuong Order ---
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setPhone(phone);
        order.setAddress(address);
        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);

        // --- 6. Goi DAO de luu vao database (co Transaction + tru ton kho) ---
        int orderId = orderDAO.createOrder(order);

        if (orderId > 0) {
            // Thanh cong: xoa cookie gio hang
            CartUtil.clearCartCookie(resp);
            req.setAttribute("orderId", orderId);
            req.getRequestDispatcher("/WEB-INF/views/order-success.jsp").forward(req, resp);
        } else {
            // That bai (co the do ton kho khong du trong DB)
            BigDecimal total = BigDecimal.ZERO;
            for (CartItem item : cartList) {
                total = total.add(item.getSubtotal());
            }
            req.setAttribute("cartList", cartList);
            req.setAttribute("totalAmount", total);
            req.setAttribute("errorMessage",
                    "Đặt hàng thất bại. Có thể sản phẩm đã hết hàng. Vui lòng thử lại.");
            req.setAttribute("customerName", customerName);
            req.setAttribute("phone", phone);
            req.setAttribute("address", address);
            req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
        }
    }
}
