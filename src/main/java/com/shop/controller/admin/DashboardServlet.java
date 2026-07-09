package com.shop.controller.admin;

import com.shop.dao.DashboardDAO;
import com.shop.model.Order;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Trang thong ke admin. URL: "/admin/dashboard"
 * NGUOI PHU TRACH: Nguoi 4 (Nguyen).
 */
@WebServlet("/admin/dashboard")
public class DashboardServlet extends HttpServlet {

    private final DashboardDAO dashboardDAO = new DashboardDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        String fromDate = req.getParameter("fromDate");
        String toDate = req.getParameter("toDate");

        if ("export".equals(action)) {
            exportToCSV(resp, fromDate, toDate);
            return;
        }

        // Lọc theo ngày
        req.setAttribute("fromDate", fromDate);
        req.setAttribute("toDate", toDate);

        req.setAttribute("totalRevenue", dashboardDAO.getTotalRevenue(fromDate, toDate));
        req.setAttribute("totalOrders", dashboardDAO.countOrders(fromDate, toDate));
        
        // Biểu đồ doanh thu 6 tháng
        Map<String, String> revenueChart = dashboardDAO.getRevenueLast6Months();
        req.setAttribute("chartLabels", revenueChart.get("labels"));
        req.setAttribute("chartData", revenueChart.get("data"));

        // Biểu đồ tròn: Tỷ trọng doanh thu theo danh mục
        Map<String, String> statusChart = dashboardDAO.getRevenueByCategory(fromDate, toDate);
        req.setAttribute("statusLabels", statusChart.get("labels"));
        req.setAttribute("statusData", statusChart.get("data"));
        
        // 5 đơn hàng mới nhất
        req.setAttribute("latestOrders", dashboardDAO.getLatestOrders(5));

        // Sản phẩm
        req.setAttribute("topProducts", dashboardDAO.getTopSellingProducts());
        req.setAttribute("outOfStock", dashboardDAO.getLowStockProducts());

        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }

    private void exportToCSV(HttpServletResponse resp, String fromDate, String toDate) throws IOException {
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"revenue_report.csv\"");
        resp.setCharacterEncoding("UTF-8");
        
        try (PrintWriter writer = resp.getWriter()) {
            writer.write("\uFEFF"); // BOM cho Excel doc duoc UTF-8
            writer.write("Thống kê từ ngày,Đến ngày\n");
            writer.write((fromDate != null ? fromDate : "Tất cả") + "," + (toDate != null ? toDate : "Tất cả") + "\n\n");
            
            writer.write("Tổng doanh thu,Tổng số đơn hàng\n");
            writer.write(dashboardDAO.getTotalRevenue(fromDate, toDate) + "," + dashboardDAO.countOrders(fromDate, toDate) + "\n\n");
            
            writer.write("5 Đơn hàng mới nhất\n");
            writer.write("ID,Khách hàng,Tổng tiền,Trạng thái,Ngày tạo\n");
            List<Map<String, Object>> latest = dashboardDAO.getLatestOrders(5);
            for (Map<String, Object> o : latest) {
                writer.write(o.get("id") + "," 
                           + "\"" + o.get("customer_name") + "\"," 
                           + o.get("total_amount") + "," 
                           + o.get("status") + "," 
                           + o.get("created_at") + "\n");
            }
        }
    }
}
