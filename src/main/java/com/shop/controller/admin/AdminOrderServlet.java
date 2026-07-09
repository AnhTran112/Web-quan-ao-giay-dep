package com.shop.controller.admin;

import com.shop.dao.OrderDAO;
import com.shop.model.Order;
import com.shop.model.OrderStatus;
import com.shop.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * Quan ly don hang (admin). URL: "/admin/orders"
 *
 * NGUOI PHU TRACH: Nguoi 3 (Khoa).
 *   doGet                : danh sach don co LOC (trang thai / SDT / khoang ngay) + PHAN TRANG.
 *   doGet?action=view&id : trang chi tiet don (mon hang, tien, lich su trang thai, ghi chu).
 *   doPost action=status : doi trang thai theo luat OrderStatus (huy don -> hoan kho).
 *   doPost action=note   : luu ghi chu noi bo cua admin.
 */
@WebServlet("/admin/orders")
public class AdminOrderServlet extends HttpServlet {

    private static final int PAGE_SIZE = 10;

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if ("view".equals(req.getParameter("action"))) {
            showDetail(req, resp);
            return;
        }

        // ===== Danh sach + bo loc + phan trang =====
        String status = trim(req.getParameter("status"));
        String phone  = trim(req.getParameter("phone"));
        String from   = trim(req.getParameter("from"));
        String to     = trim(req.getParameter("to"));
        int page = parseInt(req.getParameter("page"), 1);
        if (page < 1) page = 1;

        int total = orderDAO.countSearch(status, phone, from, to);
        int totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        if (page > totalPages) page = totalPages;

        req.setAttribute("orders", orderDAO.search(status, phone, from, to, page, PAGE_SIZE));
        req.setAttribute("totalOrders", total);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("allStatuses", OrderStatus.ALL);
        // giu lai gia tri bo loc de form va link phan trang dung lai
        req.setAttribute("fStatus", status);
        req.setAttribute("fPhone", phone);
        req.setAttribute("fFrom", from);
        req.setAttribute("fTo", to);

        req.getRequestDispatcher("/WEB-INF/views/admin/order-list.jsp").forward(req, resp);
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int id = parseInt(req.getParameter("id"), 0);
        Order order = id > 0 ? orderDAO.getById(id) : null;
        if (order == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
            return;
        }
        req.setAttribute("order", order);
        req.setAttribute("nextStatuses", OrderStatus.allowedNext(order.getStatus()));
        req.getRequestDispatcher("/WEB-INF/views/admin/order-detail.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        int id = parseInt(req.getParameter("id"), 0);
        String action = trim(req.getParameter("action"));
        String detailUrl = req.getContextPath() + "/admin/orders?action=view&id=" + id;

        switch (action) {
            case "status": {
                String newStatus = trim(req.getParameter("status"));
                try {
                    String adminName = currentAdminName(req);
                    orderDAO.updateStatus(id, newStatus, adminName);
                    com.shop.dao.ActivityLogDAO.log(adminName, "UPDATE", "ORDER", id, "Chuyển trạng thái đơn thành: " + OrderStatus.label(newStatus));
                    resp.sendRedirect(detailUrl + "&msg=" + encode("Đã chuyển đơn sang \""
                            + OrderStatus.label(newStatus) + "\"."));
                } catch (OrderDAO.OrderException e) {
                    resp.sendRedirect(detailUrl + "&err=" + encode(e.getMessage()));
                }
                break;
            }
            case "note": {
                String note = trim(req.getParameter("adminNote"));
                orderDAO.updateAdminNote(id, note.isEmpty() ? null : note);
                String adminName = currentAdminName(req);
                com.shop.dao.ActivityLogDAO.log(adminName, "UPDATE", "ORDER", id, "Cập nhật ghi chú admin");
                resp.sendRedirect(detailUrl + "&msg=" + encode("Đã lưu ghi chú."));
                break;
            }
            default:
                resp.sendRedirect(req.getContextPath() + "/admin/orders");
        }
    }

    // ========================= HELPER =========================

    /** Lay username admin dang dang nhap de ghi vao lich su trang thai. */
    private String currentAdminName(HttpServletRequest req) {
        Object admin = req.getSession().getAttribute("admin");
        if (admin instanceof User) {
            return ((User) admin).getUsername();
        }
        Object loggedInUser = req.getSession().getAttribute("loggedInUser");
        if (loggedInUser instanceof User) {
            return ((User) loggedInUser).getUsername();
        }
        return "admin";
    }

    private String trim(String s) { return s == null ? "" : s.trim(); }

    private int parseInt(String s, int def) {
        try {
            return (s == null || s.isEmpty()) ? def : Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private String encode(String s) throws IOException {
        return URLEncoder.encode(s, "UTF-8");
    }
}
