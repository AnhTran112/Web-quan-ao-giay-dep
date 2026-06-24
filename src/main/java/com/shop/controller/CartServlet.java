package com.shop.controller;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Quan ly gio hang bang Session. URL: "/cart"
 *
 * NGUOI PHU TRACH: Nguoi 2 (Anh).
 * TODO:
 *  - Lay gio hang tu session: (List<CartItem>) session.getAttribute("cart"); neu null thi tao moi.
 *  - action=add    : doc productId + quantity, dung ProductDAO.getById de lay thong tin,
 *                    neu da co trong gio thi cong don, chua co thi them CartItem moi.
 *  - action=update : doi so luong 1 dong theo productId.
 *  - action=remove : xoa 1 dong theo productId.
 *  - Luu lai list vao session roi forward/redirect ve cart.jsp.
 */
@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, javax.servlet.ServletException {
        // TODO (Anh): xu ly action=remove/update khi den bang GET (neu dung link)
        req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, javax.servlet.ServletException {
        // TODO (Anh): xu ly action=add tu form "Them vao gio" o product-detail.jsp
        doGet(req, resp);
    }
}
