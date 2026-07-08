package com.shop.controller.admin;

import com.shop.dao.OrderDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Trang thong ke admin. URL: "/admin/dashboard"
 *
 * NGUOI PHU TRACH: Nguoi 4 (Nguyen).
 */
@WebServlet("/admin/dashboard")
public class DashboardServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Dữ liệu thực từ Khoa
        req.setAttribute("totalRevenue", orderDAO.getTotalRevenue());
        req.setAttribute("totalOrders", orderDAO.countOrders());
        
        com.shop.dao.DashboardDAO dashboardDAO = new com.shop.dao.DashboardDAO();
        
        // Dữ liệu thực tế cho biểu đồ Chart.js (từ 6 tháng gần nhất)
        java.util.Map<String, String> chartDataMap = dashboardDAO.getRevenueLast6Months();
        req.setAttribute("chartLabels", chartDataMap.get("labels"));
        req.setAttribute("chartData", chartDataMap.get("data"));
        
        // Lấy danh sách sản phẩm bán chạy và sắp hết hàng qua DAO chuẩn mô hình MVC
        req.setAttribute("topProducts", dashboardDAO.getTopSellingProducts());
        req.setAttribute("outOfStock", dashboardDAO.getLowStockProducts());

        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }
}
