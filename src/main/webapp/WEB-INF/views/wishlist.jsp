<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ include file="common/header.jsp" %>
<fmt:setLocale value="vi_VN" />

<h3 class="mb-4">Sản phẩm yêu thích</h3>

<c:choose>
    <c:when test="${empty wishlist}">
        <div class="alert alert-info">Bạn chưa có sản phẩm yêu thích nào.</div>
    </c:when>
    <c:otherwise>
        <div class="row row-cols-1 row-cols-md-3 row-cols-lg-4 g-4 mb-4">
            <c:forEach var="p" items="${wishlist}">
                <div class="col" id="wishlist-item-${p.id}">
                    <div class="card h-100 product-card shadow-sm border-0 position-relative">
                        <a href="${pageContext.request.contextPath}/product?id=${p.id}">
                            <img src="${pageContext.request.contextPath}/assets/images/${p.image}" class="card-img-top p-img" alt="${p.name}">
                        </a>
                        <div class="card-body d-flex flex-column">
                            <a href="${pageContext.request.contextPath}/product?id=${p.id}" class="text-decoration-none text-dark">
                                <h5 class="card-title product-title">${p.name}</h5>
                            </a>
                            <div class="mt-auto">
                                <div class="d-flex justify-content-between align-items-center mb-2">
                                    <span class="text-danger fw-bold fs-5">
                                        <fmt:formatNumber value="${p.price * (100 - p.discountPercent) / 100}" type="number" maxFractionDigits="0"/> đ
                                    </span>
                                </div>
                                <div class="d-flex justify-content-between">
                                    <button class="btn btn-outline-danger btn-sm" onclick="removeWishlist(${p.id})">Bỏ yêu thích</button>
                                    <a href="${pageContext.request.contextPath}/product?id=${p.id}" class="btn btn-primary btn-sm">Xem chi tiết</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<script>
    function removeWishlist(id) {
        toggleWishlist(id);
        let el = document.getElementById('wishlist-item-' + id);
        if(el) el.remove();
    }
</script>

<%@ include file="common/footer.jsp" %>
