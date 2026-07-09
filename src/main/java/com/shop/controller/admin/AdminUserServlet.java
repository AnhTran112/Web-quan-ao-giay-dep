package com.shop.controller.admin;

import com.shop.dao.ActivityLogDAO;
import com.shop.dao.UserDAO;
import com.shop.model.User;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * Quản lý tài khoản người dùng (Nhân viên / Khách hàng).
 * Phân quyền: Chỉ có ADMIN mới vào được.
 */
@WebServlet("/admin/users")
public class AdminUserServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("delete".equals(action)) {
            int id = parseId(req.getParameter("id"));
            User admin = getAdmin(req);
            if (id <= 0) {
                resp.sendRedirect(req.getContextPath() + "/admin/users?error=InvalidId");
                return;
            }
            // Khong cho tu xoa chinh minh
            if (admin != null && admin.getId() == id) {
                resp.sendRedirect(req.getContextPath() + "/admin/users?error=CannotDeleteSelf");
                return;
            }
            // Khong cho xoa admin cuoi cung (tranh khoa cung khu vuc admin)
            User target = userDAO.findById(id);
            if (target != null && "ADMIN".equals(target.getRole()) && userDAO.countActiveAdmins() <= 1) {
                resp.sendRedirect(req.getContextPath() + "/admin/users?error=LastAdmin");
                return;
            }
            userDAO.deleteUser(id);
            if (admin != null) {
                ActivityLogDAO.log(admin.getUsername(), "DELETE", "USER", id, "Xóa tài khoản ID " + id);
            }
            resp.sendRedirect(req.getContextPath() + "/admin/users?msg=Deleted");
            return;
        } else if ("edit".equals(action)) {
            // Khong dung chuc nang edit rieng le vi update role/status ngay tren danh sach cho tien
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        }

        List<User> users = userDAO.findAll();
        req.setAttribute("users", users);
        req.getRequestDispatcher("/WEB-INF/views/admin/user-list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        User admin = getAdmin(req);

        if ("add".equals(action)) {
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            String fullName = req.getParameter("fullName");
            String role = req.getParameter("role"); // ADMIN, STAFF, CUSTOMER

            username = username == null ? "" : username.trim();
            fullName = fullName == null ? "" : fullName.trim();

            // Chan tao user thieu thong tin (tranh 500 do BCrypt.hashpw(null)) va role khong hop le
            if (username.isEmpty() || password == null || password.isEmpty() || !isValidRole(role)) {
                resp.sendRedirect(req.getContextPath() + "/admin/users?error=InvalidInput");
                return;
            }

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password); // UserDAO tu dong BCrypt
            newUser.setFullName(fullName);
            newUser.setRole(role);

            if (userDAO.insertUser(newUser)) {
                if (admin != null) {
                    ActivityLogDAO.log(admin.getUsername(), "CREATE", "USER", null, "Tạo tài khoản mới: " + username + " (" + role + ")");
                }
                resp.sendRedirect(req.getContextPath() + "/admin/users?msg=Created");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/users?error=UsernameExists");
            }
        } else if ("updateRoleStatus".equals(action)) {
            int id = parseId(req.getParameter("id"));
            String newRole = req.getParameter("role");
            String newStatus = req.getParameter("status"); // ACTIVE, LOCKED

            // Kiem tra dau vao hop le
            if (id <= 0 || !isValidRole(newRole) || !isValidStatus(newStatus)) {
                resp.sendRedirect(req.getContextPath() + "/admin/users?error=InvalidInput");
                return;
            }

            // Khong cho tu ha quyen/khoa chinh minh
            if (admin != null && admin.getId() == id
                    && (!"ADMIN".equals(newRole) || !"ACTIVE".equals(newStatus))) {
                resp.sendRedirect(req.getContextPath() + "/admin/users?error=CannotDemoteSelf");
                return;
            }

            // Khong cho ha quyen/khoa admin cuoi cung
            User target = userDAO.findById(id);
            if (target != null && "ADMIN".equals(target.getRole())
                    && (!"ADMIN".equals(newRole) || !"ACTIVE".equals(newStatus))
                    && userDAO.countActiveAdmins() <= 1) {
                resp.sendRedirect(req.getContextPath() + "/admin/users?error=LastAdmin");
                return;
            }

            userDAO.updateUserRoleAndStatus(id, newRole, newStatus);
            if (admin != null) {
                ActivityLogDAO.log(admin.getUsername(), "UPDATE", "USER", id, "Đổi quyền/trạng thái tài khoản ID " + id + " -> " + newRole + " / " + newStatus);
            }
            resp.sendRedirect(req.getContextPath() + "/admin/users?msg=Updated");
        }
    }

    private User getAdmin(HttpServletRequest req) {
        User u = (User) req.getSession().getAttribute("loggedInUser");
        if (u == null) u = (User) req.getSession().getAttribute("admin");
        return u;
    }

    /** Doc id an toan: tra ve -1 neu thieu hoac khong phai so. */
    private int parseId(String raw) {
        if (raw == null) return -1;
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private boolean isValidRole(String role) {
        return "ADMIN".equals(role) || "STAFF".equals(role) || "CUSTOMER".equals(role);
    }

    private boolean isValidStatus(String status) {
        return "ACTIVE".equals(status) || "LOCKED".equals(status);
    }
}
