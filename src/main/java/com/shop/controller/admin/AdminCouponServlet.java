package com.shop.controller.admin;

import com.shop.dao.CouponDAO;
import com.shop.model.Coupon;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Quan ly ma giam gia (admin). URL: "/admin/coupons"
 *
 * NGUOI PHU TRACH: Nguoi 3 (Khoa).
 *   doGet                 : danh sach ma.
 *   doGet?action=new      : form them moi.
 *   doGet?action=edit&id  : form sua.
 *   doGet?action=delete&id: xoa ma.
 *   doPost                : luu (them moi neu id rong, nguoc lai cap nhat) + validate.
 */
@WebServlet("/admin/coupons")
public class AdminCouponServlet extends HttpServlet {

    private final CouponDAO couponDAO = new CouponDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if (action == null) action = "";

        switch (action) {
            case "new":
                req.setAttribute("coupon", new Coupon());
                req.getRequestDispatcher("/WEB-INF/views/admin/coupon-form.jsp").forward(req, resp);
                break;
            case "edit": {
                Coupon c = couponDAO.getById(parseInt(req.getParameter("id"), 0));
                if (c == null) {
                    resp.sendRedirect(req.getContextPath() + "/admin/coupons");
                    return;
                }
                req.setAttribute("coupon", c);
                req.getRequestDispatcher("/WEB-INF/views/admin/coupon-form.jsp").forward(req, resp);
                break;
            }
            case "delete":
                couponDAO.delete(parseInt(req.getParameter("id"), 0));
                resp.sendRedirect(req.getContextPath() + "/admin/coupons?msg="
                        + URLEncoder.encode("Đã xóa mã giảm giá.", "UTF-8"));
                break;
            default:
                req.setAttribute("coupons", couponDAO.getAll());
                req.getRequestDispatcher("/WEB-INF/views/admin/coupon-list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        int id = parseInt(req.getParameter("id"), 0);
        String code = trim(req.getParameter("code")).toUpperCase();
        int percent = parseInt(req.getParameter("discountPercent"), 0);
        int maxUses = parseInt(req.getParameter("maxUses"), 0);
        String expiresAt = trim(req.getParameter("expiresAt"));
        boolean active = req.getParameter("active") != null;

        // ===== VALIDATE =====
        List<String> errors = new ArrayList<>();
        if (!code.matches("^[A-Z0-9]{3,50}$")) {
            errors.add("Mã phải từ 3-50 ký tự, chỉ gồm chữ và số (không dấu, không khoảng trắng).");
        }
        if (percent < 1 || percent > 100) {
            errors.add("Phần trăm giảm phải từ 1 đến 100.");
        }
        if (maxUses < 1) {
            errors.add("Số lượt dùng tối đa phải lớn hơn 0.");
        }

        Coupon c = new Coupon();
        c.setId(id);
        c.setCode(code);
        c.setDiscountPercent(percent);
        c.setMaxUses(maxUses);
        c.setExpiresAt(expiresAt.isEmpty() ? null : expiresAt);
        c.setActive(active);

        if (errors.isEmpty()) {
            boolean ok = (id == 0) ? couponDAO.insert(c) : couponDAO.update(c);
            if (ok) {
                resp.sendRedirect(req.getContextPath() + "/admin/coupons?msg="
                        + URLEncoder.encode("Đã lưu mã \"" + code + "\".", "UTF-8"));
                return;
            }
            errors.add("Lưu thất bại. Mã \"" + code + "\" có thể đã tồn tại.");
        }

        req.setAttribute("errors", errors);
        req.setAttribute("coupon", c);
        req.getRequestDispatcher("/WEB-INF/views/admin/coupon-form.jsp").forward(req, resp);
    }

    // ========================= HELPER =========================

    private String trim(String s) { return s == null ? "" : s.trim(); }

    private int parseInt(String s, int def) {
        try {
            return (s == null || s.isEmpty()) ? def : Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
