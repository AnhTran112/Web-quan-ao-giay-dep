package com.shop.controller.admin;

import com.shop.dao.DashboardDAO;
import com.shop.model.Order;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
            exportToExcel(resp, fromDate, toDate);
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

    private void exportToExcel(HttpServletResponse resp, String fromDate, String toDate) throws IOException {
        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("Content-Disposition", "attachment; filename=\"revenue_report.xlsx\"");
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Báo cáo doanh thu");
            
            Row row0 = sheet.createRow(0);
            row0.createCell(0).setCellValue("Thống kê từ ngày");
            row0.createCell(1).setCellValue("Đến ngày");
            
            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue(fromDate != null && !fromDate.isEmpty() ? fromDate : "Tất cả");
            row1.createCell(1).setCellValue(toDate != null && !toDate.isEmpty() ? toDate : "Tất cả");
            
            Row row3 = sheet.createRow(3);
            row3.createCell(0).setCellValue("Tổng doanh thu");
            row3.createCell(1).setCellValue("Tổng số đơn hàng");
            
            Row row4 = sheet.createRow(4);
            row4.createCell(0).setCellValue(dashboardDAO.getTotalRevenue(fromDate, toDate));
            row4.createCell(1).setCellValue(dashboardDAO.countOrders(fromDate, toDate));
            
            Row row6 = sheet.createRow(6);
            row6.createCell(0).setCellValue("5 Đơn hàng mới nhất");
            
            Row row7 = sheet.createRow(7);
            row7.createCell(0).setCellValue("ID");
            row7.createCell(1).setCellValue("Khách hàng");
            row7.createCell(2).setCellValue("Tổng tiền");
            row7.createCell(3).setCellValue("Trạng thái");
            row7.createCell(4).setCellValue("Ngày tạo");
            
            List<Map<String, Object>> latest = dashboardDAO.getLatestOrders(5);
            int r = 8;
            for (Map<String, Object> o : latest) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(o.get("id") != null ? o.get("id").toString() : "");
                row.createCell(1).setCellValue(o.get("customer_name") != null ? o.get("customer_name").toString() : "");
                row.createCell(2).setCellValue(o.get("total_amount") != null ? Double.parseDouble(o.get("total_amount").toString()) : 0);
                row.createCell(3).setCellValue(o.get("status") != null ? o.get("status").toString() : "");
                row.createCell(4).setCellValue(o.get("created_at") != null ? o.get("created_at").toString() : "");
            }
            
            // Tự động điều chỉnh độ rộng cột
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(resp.getOutputStream());
        }
    }
}
