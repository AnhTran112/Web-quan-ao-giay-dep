package com.shop.controller.admin;

import com.shop.dao.ActivityLogDAO;
import com.shop.model.ActivityLog;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/logs")
public class AdminActivityLogServlet extends HttpServlet {
    private final ActivityLogDAO logDAO = new ActivityLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ActivityLog> logs = logDAO.getAll();
        req.setAttribute("logs", logs);
        req.getRequestDispatcher("/WEB-INF/views/admin/activity-log.jsp").forward(req, resp);
    }
}
