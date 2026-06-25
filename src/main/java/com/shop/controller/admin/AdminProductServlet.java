package com.shop.controller.admin;

import com.shop.dao.CategoryDAO;
import com.shop.dao.ProductDAO;
import com.shop.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Quan ly san pham (admin). URL: "/admin/products"
 * action = list | new | edit | delete
 *
 * NGUOI PHU TRACH: Nguoi 1 (Hoang).
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
                // Them moi: chi can danh sach danh muc cho o select, khong co san pham cu
                req.setAttribute("categories", categoryDAO.getAll());
                req.getRequestDispatcher("/WEB-INF/views/admin/product-form.jsp").forward(req, resp);
                break;
            case "edit":
                // Sua: doc id tren URL, lay san pham cu de form hien du lieu
                int editId = Integer.parseInt(req.getParameter("id"));
                req.setAttribute("product", productDAO.getById(editId));
                req.setAttribute("categories", categoryDAO.getAll());
                req.getRequestDispatcher("/WEB-INF/views/admin/product-form.jsp").forward(req, resp);
                break;
            case "delete":
                int deleteId = Integer.parseInt(req.getParameter("id"));
                productDAO.delete(deleteId);
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
        // Doc du lieu tu form
        String idParam = req.getParameter("id");
        String name = req.getParameter("name");
        int categoryId = Integer.parseInt(req.getParameter("categoryId"));
        BigDecimal price = new BigDecimal(req.getParameter("price"));
        int quantity = Integer.parseInt(req.getParameter("quantity"));
        String image = req.getParameter("image");
        String description = req.getParameter("description");

        // Dong goi vao object Product
        Product p = new Product();
        p.setName(name);
        p.setCategoryId(categoryId);
        p.setPrice(price);
        p.setQuantity(quantity);
        p.setImage(image);
        p.setDescription(description);

        // id rong -> them moi; co id -> cap nhat
        if (idParam == null || idParam.isEmpty()) {
            productDAO.insert(p);
        } else {
            p.setId(Integer.parseInt(idParam));
            productDAO.update(p);
        }

        resp.sendRedirect(req.getContextPath() + "/admin/products");
    }
}
