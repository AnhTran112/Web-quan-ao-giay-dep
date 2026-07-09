package com.shop.filter;

import com.shop.dao.UserDAO;
import com.shop.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class RememberMeFilter implements Filter {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession();

        if (session.getAttribute("loggedInUser") == null) {
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("remember_me".equals(cookie.getName())) {
                        String token = cookie.getValue();
                        User user = userDAO.findByRememberToken(token);
                        if (user != null && !"LOCKED".equals(user.getStatus())) {
                            session.setAttribute("loggedInUser", user);
                            session.setAttribute("admin", user); // ho tro cho code cu
                        }
                        break;
                    }
                }
            }
        }
        
        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {}
}
