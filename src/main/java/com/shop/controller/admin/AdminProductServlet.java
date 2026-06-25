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
        // Doc du lieu tu form (de dang String de tu kiem tra truoc khi doi kieu)
        String idParam = req.getParameter("id");
        String name = req.getParameter("name");
        String categoryIdParam = req.getParameter("categoryId");
        String priceParam = req.getParameter("price");
        String quantityParam = req.getParameter("quantity");
        String image = req.getParameter("image");
        String description = req.getParameter("description");

        // ===== VALIDATE PHIA SERVER =====
        String error = null;
        BigDecimal price = null;
        int quantity = 0;
        int categoryId = 0;

        if (name == null || name.trim().isEmpty()) {
            error = "Tên sản phẩm không được để trống.";
        } else {
            try {
                price = new BigDecimal(priceParam);
                quantity = Integer.parseInt(quantityParam);
                categoryId = Integer.parseInt(categoryIdParam);
                if (price.signum() < 0) error = "Giá phải lớn hơn hoặc bằng 0.";
                else if (quantity < 0)  error = "Số lượng phải lớn hơn hoặc bằng 0.";
            } catch (NumberFormatException e) {
                error = "Giá và số lượng phải là số hợp lệ.";
            }
        }

        // Dong goi vao object Product
        Product p = new Product();
        p.setName(name);
        p.setCategoryId(categoryId);
        p.setPrice(price);
        p.setQuantity(quantity);
        p.setImage(image);
        p.setDescription(description);
        if (idParam != null && !idParam.isEmpty()) p.setId(Integer.parseInt(idParam));

        // Neu co loi -> quay lai form, giu lai du lieu da nhap + bao loi
        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("product", p);
            req.setAttribute("categories", categoryDAO.getAll());
            req.getRequestDispatcher("/WEB-INF/views/admin/product-form.jsp").forward(req, resp);
            return;
        }

        // id rong -> them moi; co id -> cap nhat
        if (idParam == null || idParam.isEmpty()) {
            productDAO.insert(p);
        } else {
            productDAO.update(p);
        }

        resp.sendRedirect(req.getContextPath() + "/admin/products");
    }
}
