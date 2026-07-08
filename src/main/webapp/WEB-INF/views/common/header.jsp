<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Shop Giày Dép</title>
    <!-- Bootstrap 5 qua CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.css?v=7" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/home">ShoeShop</a>
        <%-- Nut hamburger: hien tren man hinh nho de mo/dong menu --%>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                data-bs-target="#mainNav" aria-controls="mainNav"
                aria-expanded="false" aria-label="Mở menu">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="mainNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/track-order">Tra cứu đơn hàng</a></li>
            </ul>
            <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-light mt-2 mt-lg-0 position-relative">
                Giỏ hàng
                <span id="cartBadge" class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger" style="display: none;">
                    0
                </span>
            </a>
        </div>
    </div>
</nav>
<script>
    document.addEventListener("DOMContentLoaded", function() {
        function getCookie(name) {
            let match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
            if (match) return decodeURIComponent(match[2].replace(/\+/g, ' '));
            return "";
        }
        let cartStr = getCookie("cart");
        let count = 0;
        if (cartStr) {
            let items = cartStr.split(",");
            for (let i = 0; i < items.length; i++) {
                if (items[i].trim() !== "") {
                    let parts = items[i].split(":");
                    if (parts.length === 3) {
                        count += parseInt(parts[2]) || 0;
                    }
                }
            }
        }
        if (count > 0) {
            let badge = document.getElementById("cartBadge");
            badge.innerText = count;
            badge.style.display = "inline-block";
        }
    });
</script>
</nav>
<div class="container my-4">
