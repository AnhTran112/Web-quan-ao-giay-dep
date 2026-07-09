<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đã xảy ra lỗi</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #f8f9fa; }
        .error-container { margin-top: 100px; text-align: center; }
        .error-code { font-size: 8rem; font-weight: bold; color: #dc3545; }
        .error-message { font-size: 1.5rem; margin-bottom: 30px; }
    </style>
</head>
<body>
<div class="container error-container">
    <%
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String message = "Đã có lỗi xảy ra trong hệ thống.";
        
        if (statusCode != null) {
            if (statusCode == 404) {
                message = "Không tìm thấy trang bạn yêu cầu!";
            } else if (statusCode == 403) {
                message = "Bạn không có quyền truy cập vào khu vực này!";
            } else if (statusCode == 500) {
                message = "Lỗi máy chủ nội bộ. Xin vui lòng thử lại sau.";
            }
        }
    %>
    
    <div class="error-code"><%= statusCode != null ? statusCode : "LỖI" %></div>
    <div class="error-message text-muted"><%= message %></div>
    
    <a href="${pageContext.request.contextPath}/" class="btn btn-primary btn-lg">Quay về Trang chủ</a>
    
    <c:if test="${not empty requestScope['javax.servlet.error.exception']}">
        <div class="mt-5 text-start">
            <p class="text-muted"><small>Chi tiết lỗi (Dành cho nhà phát triển):</small></p>
            <pre class="bg-light p-3 border rounded" style="font-size: 12px; max-height: 200px; overflow-y: scroll;">
                ${requestScope['javax.servlet.error.exception']}
            </pre>
        </div>
    </c:if>
</div>
</body>
</html>
