package com.shop.controller.admin;

import com.shop.dao.CategoryDAO;
import com.shop.dao.ProductDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Quan ly san pham (admin). URL: "/admin/products"
 * action = list | new | edit | delete
 *
 * NGUOI PHU TRACH: Nguoi 1 (Hoang).
 * Hien tai: list + form (them moi) da chay. Con TODO: load san pham khi edit, luu, xoa.
 */
@WebServlet("/admin/products")
public class AdminProductServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "new":
            case "edit":
                // TODO (Hoang): neu la edit -> doc id, productDAO.getById(id),
                //   req.setAttribute("product", p) de form hien du lieu cu.
                req.setAttribute("categories", categoryDAO.getAll());
                req.getRequestDispatcher("/WEB-INF/views/admin/product-form.jsp").forward(req, resp);
                break;
            case "delete":
                // TODO (Hoang): int id = ...; productDAO.delete(id);
                resp.sendRedirect(req.getContextPath() + "/admin/products");
                break;
            default:
                req.setAttribute("products", productDAO.getAll());
                req.getRequestDispatcher("/WEB-INF/views/admin/product-list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // TODO (Hoang): doc form (id, name, categoryId, price, quantity, image, description);
        //   id rong -> productDAO.insert(p); nguoc lai -> productDAO.update(p).
        //   (Nang cap) upload anh bang @MultipartConfig + Part.
        resp.sendRedirect(req.getContextPath() + "/admin/products");
    }
}
