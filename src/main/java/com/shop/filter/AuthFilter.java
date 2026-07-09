package com.shop.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

import com.shop.model.User;

/**
 * Chan truy cap khu vuc /admin/*
 */
@WebFilter("/admin/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getServletPath();
        if (path.startsWith("/admin/login")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession();
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            loggedInUser = (User) session.getAttribute("admin");
        }

        if (loggedInUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = loggedInUser.getRole();

        // Khách hàng không được vào admin
        if ("CUSTOMER".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập khu vực này.");
            return;
        }

        // STAFF chỉ được phép xem/xử lý đơn hàng, dashboard, đổi mật khẩu và đăng xuất.
        // Cấm truy cập: products, categories, coupons, users, logs.
        if ("STAFF".equals(role)) {
            if (path.startsWith("/admin/products") || 
                path.startsWith("/admin/categories") || 
                path.startsWith("/admin/coupons") || 
                path.startsWith("/admin/users") || 
                path.startsWith("/admin/logs")) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Nhân viên không có quyền truy cập chức năng này.");
                return;
            }
        }

        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {}
}
