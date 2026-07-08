package com.shop.controller;

import com.shop.dao.CategoryDAO;
import com.shop.dao.ProductDAO;
import com.shop.dao.ProductImageDAO;
import com.shop.dao.ReviewDAO;
import com.shop.model.Category;
import com.shop.model.Product;
import com.shop.model.ProductImage;
import com.shop.model.Review;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Chi tiet san pham. URL: "/product?id=5"
 */
@WebServlet("/product")
public class ProductDetailServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ProductImageDAO imageDAO = new ProductImageDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        Product product = productDAO.getById(id);
        req.setAttribute("product", product);

        // Tim ten danh mux cua san pham (de hien o bang thong so) bang cach duyet danh sach danh muc.
        // (Khong dung CategoryDAO.getById vi ham do thuoc phan viec cua nguoi khac, con de TODO.)
        if (product != null) {
            Category category = null;
            for (Category cat : categoryDAO.getAll()) {
                if (cat.getId() == product.getCategoryId()) {
                    category = cat;
                    req.setAttribute("categoryName", cat.getName());
                    req.setAttribute("category", cat);
                    break;
                }
            }

            // 1. Gallery
            List<ProductImage> images = imageDAO.getByProductId(id);
            req.setAttribute("images", images);

            // 2. Đánh giá
            List<Review> reviews = reviewDAO.getByProductId(id);
            req.setAttribute("reviews", reviews);

            // Tính điểm TB
            if (!reviews.isEmpty()) {
                double avg = 0;
                for (Review r : reviews) avg += r.getRating();
                req.setAttribute("avgRating", avg / reviews.size());
            }

            // 3. Sản phẩm liên quan
            List<Product> categoryProducts = productDAO.filter(product.getCategoryId(), null, null, null, "newest");
            List<Product> related = new ArrayList<>();
            for (Product p : categoryProducts) {
                if (p.getId() != id) {
                    related.add(p);
                    if (related.size() == 4) break; // chỉ lấy 4
                }
            }
            req.setAttribute("relatedProducts", related);

            // 4. Recently viewed
            updateRecentlyViewedCookie(req, resp, id);
            List<Product> recentlyViewed = getRecentlyViewed(req);
            req.setAttribute("recentlyViewed", recentlyViewed);
        }

        req.getRequestDispatcher("/WEB-INF/views/product-detail.jsp").forward(req, resp);
    }

    private void updateRecentlyViewedCookie(HttpServletRequest req, HttpServletResponse resp, int productId) {
        String cookieName = "recentlyViewed";
        String val = "";
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (cookieName.equals(c.getName())) {
                    try { val = URLDecoder.decode(c.getValue(), "UTF-8"); } catch (Exception ignored) {}
                    break;
                }
            }
        }
        
        List<String> items = new ArrayList<>(Arrays.asList(val.split(",")));
        items.removeIf(String::isEmpty);
        items.remove(String.valueOf(productId)); // xóa nếu đã có
        items.add(0, String.valueOf(productId)); // thêm vào đầu
        
        if (items.size() > 10) items = items.subList(0, 10); // giữ 10 SP
        
        try {
            Cookie c = new Cookie(cookieName, URLEncoder.encode(String.join(",", items), "UTF-8"));
            c.setMaxAge(60 * 60 * 24 * 7);
            c.setPath("/");
            resp.addCookie(c);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private List<Product> getRecentlyViewed(HttpServletRequest req) {
        List<Product> list = new ArrayList<>();
        String cookieName = "recentlyViewed";
        String val = "";
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (cookieName.equals(c.getName())) {
                    try { val = URLDecoder.decode(c.getValue(), "UTF-8"); } catch (Exception ignored) {}
                    break;
                }
            }
        }
        String[] parts = val.split(",");
        for (String pId : parts) {
            if (!pId.trim().isEmpty()) {
                Product p = productDAO.getById(Integer.parseInt(pId.trim()));
                if (p != null) list.add(p);
            }
        }
        return list;
    }
}
