package com.shop.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Chan truy cap moi trang /admin/* neu chua dang nhap.
 * Ngoai tru chinh trang dang nhap (/admin/login).
 */
@WebFilter("/admin/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getServletPath();
        boolean isLoginPage = path.equals("/admin/login");
        boolean loggedIn = req.getSession().getAttribute("admin") != null;

        if (isLoginPage || loggedIn) {
            chain.doFilter(request, response); // cho qua
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/login");
        }
    }
}
