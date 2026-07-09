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
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link href="${pageContext.request.contextPath}/assets/css/style.css?v=8" rel="stylesheet">
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
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/wishlist">Yêu thích <span id="wishlistBadge" class="badge bg-danger ms-1" style="display:none;">0</span></a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/track-order">Tra cứu đơn hàng</a></li>
            </ul>
            <ul class="navbar-nav ms-3 me-3">
                <c:choose>
                    <c:when test="${not empty sessionScope.loggedInUser}">
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle text-white" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                Chào, ${sessionScope.loggedInUser.fullName}
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/account">Tài khoản của tôi</a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/account?tab=orders">Lịch sử mua hàng</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/logout">Đăng xuất</a></li>
                            </ul>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="nav-item"><a class="nav-link text-white" href="${pageContext.request.contextPath}/login">Đăng nhập</a></li>
                        <li class="nav-item"><a class="nav-link text-white" href="${pageContext.request.contextPath}/register">Đăng ký</a></li>
                    </c:otherwise>
                </c:choose>
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
        updateWishlistBadge();
    });

    // --- AJAX Add to Cart ---
    function addToCartAjax(productId, quantity, variantId = 0) {
        fetch('${pageContext.request.contextPath}/api/cart/add', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'productId=' + productId + '&variantId=' + variantId + '&quantity=' + quantity
        })
        .then(response => response.json())
        .then(data => {
            if(data.success) {
                let badge = document.getElementById("cartBadge");
                badge.innerText = data.totalItems;
                badge.style.display = "inline-block";
                showToast('Đã thêm sản phẩm vào giỏ hàng!');
            } else {
                showToast('Lỗi: ' + data.message, true);
            }
        }).catch(err => {
            showToast('Lỗi kết nối!', true);
        });
    }

    // --- AJAX Wishlist (Cookie) ---
    function toggleWishlist(productId) {
        let wl = getCookie("wishlist");
        let items = wl ? wl.split(",") : [];
        let index = items.indexOf(productId.toString());
        if (index > -1) {
            items.splice(index, 1);
            showToast('Đã bỏ yêu thích!');
        } else {
            items.push(productId);
            showToast('Đã thêm vào yêu thích!');
        }
        document.cookie = "wishlist=" + items.join(",") + "; max-age=" + (60*60*24*30) + "; path=/";
        updateWishlistBadge();
    }
    function updateWishlistBadge() {
        let wl = getCookie("wishlist");
        let items = wl ? wl.split(",").filter(x => x !== "") : [];
        let badge = document.getElementById("wishlistBadge");
        if(items.length > 0) {
            badge.innerText = items.length;
            badge.style.display = "inline-block";
        } else {
            badge.style.display = "none";
        }
    }
    function getCookie(name) {
        let match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
        if (match) return decodeURIComponent(match[2].replace(/\+/g, ' '));
        return "";
    }

    // --- Toast Notification ---
    function showToast(message, isError = false) {
        let toastEl = document.getElementById('globalToast');
        let toastBody = document.getElementById('globalToastBody');
        toastBody.innerText = message;
        if(isError) {
            toastEl.classList.remove('text-bg-success');
            toastEl.classList.add('text-bg-danger');
        } else {
            toastEl.classList.remove('text-bg-danger');
            toastEl.classList.add('text-bg-success');
        }
        let toast = new bootstrap.Toast(toastEl);
        toast.show();
    }
</script>

<!-- Global Toast Container -->
<div class="toast-container position-fixed bottom-0 end-0 p-3" style="z-index: 1100">
  <div id="globalToast" class="toast align-items-center text-bg-success border-0" role="alert" aria-live="assertive" aria-atomic="true">
    <div class="d-flex">
      <div class="toast-body" id="globalToastBody">
        Nội dung thông báo
      </div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
    </div>
  </div>
</div>

<div class="container my-4">
