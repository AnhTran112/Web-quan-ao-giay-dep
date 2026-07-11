package com.shop.controller.api;

import com.shop.dao.ReviewDAO;
import com.shop.model.Review;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/review")
public class ReviewApiServlet extends HttpServlet {
    private final ReviewDAO reviewDAO = new ReviewDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        int productId = parseInt(req.getParameter("productId"), 0);
        String phone = req.getParameter("phone");
        int rating = parseInt(req.getParameter("rating"), 5);
        String comment = req.getParameter("comment");

        if (productId <= 0 || phone == null || phone.trim().isEmpty()) {
            resp.getWriter().write("{\"success\":false,\"message\":\"Thiếu thông tin bắt buộc.\"}");
            return;
        }

        phone = phone.trim();
        // Kiểm tra xem đã mua chưa
        if (!reviewDAO.hasPurchased(phone, productId)) {
            resp.getWriter().write("{\"success\":false,\"message\":\"Số điện thoại này chưa từng mua sản phẩm hoặc đơn hàng đã bị hủy.\"}");
            return;
        }

        Review r = new Review();
        r.setProductId(productId);
        r.setPhone(phone);
        r.setRating(rating);
        r.setComment(comment);

        if (reviewDAO.insert(r)) {
            resp.getWriter().write("{\"success\":true,\"message\":\"Đánh giá của bạn đã được gửi thành công!\"}");
        } else {
            resp.getWriter().write("{\"success\":false,\"message\":\"Có lỗi xảy ra, vui lòng thử lại sau.\"}");
        }
    }

    private int parseInt(String s, int def) {
        try { return (s == null || s.isEmpty()) ? def : Integer.parseInt(s); }
        catch (Exception e) { return def; }
    }
}
