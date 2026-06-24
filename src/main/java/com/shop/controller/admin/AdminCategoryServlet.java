package com.shop.controller.admin;

import com.shop.dao.CategoryDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Quan ly danh muc (admin). URL: "/admin/categories"
 * action = list | new | edit | delete
 *
 * NGUOI PHU TRACH: Nguoi 4 (Nguyen).
 * Hien tai: hien thi danh sach (doGet list) da chay. Phan luu/sua/xoa con TODO.
 */
@WebServlet("/admin/categories")
public class AdminCategoryServlet extends HttpServlet {

    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "new":
                req.getRequestDispatcher("/WEB-INF/views/admin/category-form.jsp").forward(req, resp);
                break;
            case "edit":
                // TODO (Nguyen): int id = ...; req.setAttribute("category", categoryDAO.getById(id));
                req.getRequestDispatcher("/WEB-INF/views/admin/category-form.jsp").forward(req, resp);
                break;
            case "delete":
                // TODO (Nguyen): int id = ...; categoryDAO.delete(id);
                resp.sendRedirect(req.getContextPath() + "/admin/categories");
                break;
            default:
                req.setAttribute("categories", categoryDAO.getAll());
                req.getRequestDispatcher("/WEB-INF/views/admin/category-list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // TODO (Nguyen): doc form (id, name, description);
        //   neu id rong -> categoryDAO.insert(c); nguoc lai -> categoryDAO.update(c);
        resp.sendRedirect(req.getContextPath() + "/admin/categories");
    }
}
