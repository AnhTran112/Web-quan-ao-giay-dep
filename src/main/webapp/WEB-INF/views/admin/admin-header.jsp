<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trang quản trị</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-dark bg-dark">
    <div class="container-fluid">
        <span class="navbar-brand">⚙️ Admin Panel</span>
        <div>
            <a class="btn btn-sm btn-outline-light" href="${pageContext.request.contextPath}/admin/dashboard">Thống kê</a>
            <a class="btn btn-sm btn-outline-light" href="${pageContext.request.contextPath}/admin/products">Sản phẩm</a>
            <a class="btn btn-sm btn-outline-light" href="${pageContext.request.contextPath}/admin/categories">Danh mục</a>
            <a class="btn btn-sm btn-outline-light" href="${pageContext.request.contextPath}/admin/orders">Đơn hàng</a>
            <a class="btn btn-sm btn-outline-warning" href="${pageContext.request.contextPath}/admin/logout">Đăng xuất</a>
        </div>
    </div>
</nav>
<div class="container my-4">
