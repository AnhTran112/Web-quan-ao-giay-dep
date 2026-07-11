package com.shop.controller;

import com.shop.dao.CategoryDAO;
import com.shop.dao.ProductDAO;
import com.shop.model.Category;
import com.shop.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * Trang chu: hien thi danh sach san pham + loc theo danh muc / gia.
 * URL: "/" hoac "/home"
 */
@WebServlet(urlPatterns = {"", "/home"})
public class HomeServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Doc tham so loc tu URL (?categoryId=1&minPrice=...&maxPrice=...)
        Integer categoryId = parseInt(req.getParameter("categoryId"));
        Long minPrice = parseLong(req.getParameter("minPrice"));
        Long maxPrice = parseLong(req.getParameter("maxPrice"));
        String keyword = req.getParameter("keyword"); // tim theo ten san pham
        String sort = req.getParameter("sort"); // tieu chi sap xep

        // Lay toan bo san pham (dung ca de tinh gia cao nhat cho thanh truot loc)
        List<Product> all = productDAO.getAll();

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        List<Product> products;
        
        int page = 1;
        int pageSize = 6;
        Integer paramPage = parseInt(req.getParameter("page"));
        if (paramPage != null && paramPage > 0) {
            page = paramPage;
        }

        int totalPages = 1;

        if (categoryId == null && minPrice == null && maxPrice == null && !hasKeyword && (sort == null || sort.isEmpty() || sort.equals("newest"))) {
            // Unfiltered: use DB pagination
            int totalCount = productDAO.getTotalCount();
            totalPages = (int) Math.ceil((double) totalCount / pageSize);
            products = productDAO.getPage((page - 1) * pageSize, pageSize);
        } else {
            // Filtered: apply filter then in-memory pagination
            List<Product> filtered = productDAO.filter(categoryId, minPrice, maxPrice, keyword, sort);
            totalPages = (int) Math.ceil((double) filtered.size() / pageSize);
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, filtered.size());
            if (start > filtered.size()) {
                start = 0; end = 0;
            }
            products = filtered.subList(start, end);
        }
        
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        List<Category> categories = categoryDAO.getAll();

        // Gia cao nhat -> gioi han phai cua thanh truot. Lam tron len boi 100.000 cho dep.
        long priceMax = 1_000_000;
        for (Product p : all) {
            if (p.getPrice() != null) priceMax = Math.max(priceMax, p.getPrice().longValue());
        }
        priceMax = ((priceMax + 99_999) / 100_000) * 100_000;

        req.setAttribute("products", products);
        req.setAttribute("categories", categories);
        req.setAttribute("priceMax", priceMax);
        req.setAttribute("minPrice", minPrice); // giu lai de thanh truot hien dung vi tri da chon
        req.setAttribute("maxPrice", maxPrice);
        req.setAttribute("keyword", keyword);   // giu lai chu da go trong o tim kiem
        req.setAttribute("categoryId", categoryId); // giu lai danh muc da chon
        req.setAttribute("sort", sort);         // giu lai tieu chi sap xep
        req.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(req, resp);
    }

    private Integer parseInt(String s) {
        try { return (s == null || s.isEmpty()) ? null : Integer.parseInt(s); }
        catch (NumberFormatException e) { return null; }
    }

    private Long parseLong(String s) {
        try { return (s == null || s.isEmpty()) ? null : Long.parseLong(s); }
        catch (NumberFormatException e) { return null; }
    }
}
