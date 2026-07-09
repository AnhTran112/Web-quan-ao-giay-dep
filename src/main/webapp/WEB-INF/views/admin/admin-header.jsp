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
    <link href="${pageContext.request.contextPath}/assets/css/style.css?v=8" rel="stylesheet">
</head>
<body class="admin-body">
<nav class="navbar navbar-dark bg-dark admin-navbar">
    <div class="container-fluid">
        <span class="navbar-brand fw-bold">Admin Panel</span>
        <div class="admin-nav d-flex gap-1 flex-wrap align-items-center">
            <a data-nav="dashboard"  href="${pageContext.request.contextPath}/admin/dashboard">Thống kê</a>
            <a data-nav="products"   href="${pageContext.request.contextPath}/admin/products">Sản phẩm</a>
            <a data-nav="categories" href="${pageContext.request.contextPath}/admin/categories">Danh mục</a>
            <a data-nav="orders"     href="${pageContext.request.contextPath}/admin/orders">Đơn hàng</a>
            <a data-nav="coupons"    href="${pageContext.request.contextPath}/admin/coupons">Mã giảm giá</a>
            <c:if test="${loggedInUser.role == 'ADMIN' || admin.role == 'ADMIN'}">
                <a data-nav="users" href="${pageContext.request.contextPath}/admin/users">Tài khoản</a>
                <a data-nav="logs"  href="${pageContext.request.contextPath}/admin/logs">Nhật ký (Audit)</a>
            </c:if>
            <a class="btn btn-sm btn-warning ms-2" href="${pageContext.request.contextPath}/admin/logout">Đăng xuất</a>
        </div>
    </div>
</nav>
<script>
    // To dam muc dang xem tren thanh dieu huong admin
    (function () {
        var path = location.pathname;
        document.querySelectorAll('.admin-nav a[data-nav]').forEach(function (a) {
            if (path.indexOf('/admin/' + a.getAttribute('data-nav')) >= 0) a.classList.add('active');
        });
    })();
</script>
<div class="container my-4">
