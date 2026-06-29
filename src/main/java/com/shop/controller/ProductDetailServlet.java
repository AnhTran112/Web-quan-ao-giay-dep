package com.shop.controller;

import com.shop.dao.CategoryDAO;
import com.shop.dao.ProductDAO;
import com.shop.model.Category;
import com.shop.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Chi tiet san pham. URL: "/product?id=5"
 */
@WebServlet("/product")
public class ProductDetailServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        Product product = productDAO.getById(id);
        req.setAttribute("product", product);

        // Tim ten danh mux cua san pham (de hien o bang thong so) bang cach duyet danh sach danh muc.
        // (Khong dung CategoryDAO.getById vi ham do thuoc phan viec cua nguoi khac, con de TODO.)
        if (product != null) {
            for (Category cat : categoryDAO.getAll()) {
                if (cat.getId() == product.getCategoryId()) {
                    req.setAttribute("categoryName", cat.getName());
                    break;
                }
            }
        }

        req.getRequestDispatcher("/WEB-INF/views/product-detail.jsp").forward(req, resp);
    }
}
