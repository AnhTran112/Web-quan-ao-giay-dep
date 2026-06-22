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

        List<Product> products;
        if (categoryId == null && minPrice == null && maxPrice == null) {
            products = productDAO.getAll();
        } else {
            products = productDAO.filter(categoryId, minPrice, maxPrice);
        }
        List<Category> categories = categoryDAO.getAll();

        req.setAttribute("products", products);
        req.setAttribute("categories", categories);
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
