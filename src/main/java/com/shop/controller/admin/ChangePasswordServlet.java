package com.shop.controller.admin;

import com.shop.dao.UserDAO;
import com.shop.model.User;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet({"/admin/change-password", "/change-password"})
public class ChangePasswordServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User loggedInUser = (User) req.getSession().getAttribute("loggedInUser");
        if (loggedInUser == null) {
            loggedInUser = (User) req.getSession().getAttribute("admin");
        }

        if (loggedInUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        boolean isAdmin = "ADMIN".equals(loggedInUser.getRole()) || "STAFF".equals(loggedInUser.getRole());
        if (!isAdmin) {
            // Khach doi mat khau ngay trong trang tai khoan (khong co JSP rieng)
            resp.sendRedirect(req.getContextPath() + "/account?tab=password");
            return;
        }
        req.setAttribute("postUrl", req.getContextPath() + "/admin/change-password");
        req.getRequestDispatcher("/WEB-INF/views/admin/change-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User loggedInUser = (User) req.getSession().getAttribute("loggedInUser");
        if (loggedInUser == null) {
            loggedInUser = (User) req.getSession().getAttribute("admin");
        }

        if (loggedInUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        boolean isAdmin = "ADMIN".equals(loggedInUser.getRole()) || "STAFF".equals(loggedInUser.getRole());
        String jspPath = isAdmin ? "/WEB-INF/views/admin/change-password.jsp" : "/WEB-INF/views/change-password.jsp";
        String postUrl = isAdmin ? "/admin/change-password" : "/change-password";
        req.setAttribute("postUrl", req.getContextPath() + postUrl);

        String oldPassword = req.getParameter("oldPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        // Chong NPE khi request thieu tham so
        if (oldPassword == null || oldPassword.isEmpty()
                || newPassword == null || newPassword.isEmpty()
                || confirmPassword == null || confirmPassword.isEmpty()) {
            if (isAdmin) {
                req.setAttribute("error", "Vui lòng nhập đầy đủ thông tin!");
                req.getRequestDispatcher(jspPath).forward(req, resp);
            } else {
                resp.sendRedirect(req.getContextPath() + "/account?tab=password&error=PasswordMismatch");
            }
            return;
        }

        if (newPassword.length() < 6) {
            if (isAdmin) {
                req.setAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự!");
                req.getRequestDispatcher(jspPath).forward(req, resp);
            } else {
                resp.sendRedirect(req.getContextPath() + "/account?tab=password&error=PasswordMismatch");
            }
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            if (isAdmin) {
                req.setAttribute("error", "Mật khẩu xác nhận không khớp!");
                req.getRequestDispatcher(jspPath).forward(req, resp);
            } else {
                resp.sendRedirect(req.getContextPath() + "/account?tab=password&error=PasswordMismatch");
            }
            return;
        }

        // Lấy thông tin user mới nhất từ DB
        User dbUser = userDAO.findByUsername(loggedInUser.getUsername());

        if (dbUser != null && BCrypt.checkpw(oldPassword, dbUser.getPassword())) {
            if (userDAO.updatePassword(dbUser.getId(), newPassword)) {
                if (isAdmin) {
                    req.setAttribute("success", "Đổi mật khẩu thành công!");
                    req.getRequestDispatcher(jspPath).forward(req, resp);
                } else {
                    resp.sendRedirect(req.getContextPath() + "/account?tab=password&success=PasswordChanged");
                }
            } else {
                if (isAdmin) {
                    req.setAttribute("error", "Lỗi cập nhật CSDL. Vui lòng thử lại!");
                    req.getRequestDispatcher(jspPath).forward(req, resp);
                } else {
                    resp.sendRedirect(req.getContextPath() + "/account?tab=password&error=UpdateFailed");
                }
            }
        } else {
            if (isAdmin) {
                req.setAttribute("error", "Mật khẩu cũ không chính xác!");
                req.getRequestDispatcher(jspPath).forward(req, resp);
            } else {
                resp.sendRedirect(req.getContextPath() + "/account?tab=password&error=WrongOldPassword");
            }
        }
    }
}
