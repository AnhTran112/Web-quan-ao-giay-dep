package com.shop.filter;

import com.shop.util.CartUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Filter to extract cart count from cookies and make it available globally
 * in the request scope. It skips static resources.
 */
@WebFilter(urlPatterns = "/*")
public class CartFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI();
        
        // Skip static resources
        if (path.startsWith(req.getContextPath() + "/assets/") || path.matches(".*\\.(css|js|jpg|jpeg|png|gif|ico)$")) {
            chain.doFilter(request, response);
            return;
        }

        // Calculate total cart items and set it to request attribute
        int cartCount = CartUtil.getCartCount(req.getCookies());
        req.setAttribute("cartCount", cartCount);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
    }
}
