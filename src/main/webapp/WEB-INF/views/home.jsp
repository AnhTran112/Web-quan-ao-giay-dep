<%@ include file="common/header.jsp" %>

<!-- Banner chao mung -->
<div class="hero-banner">
    <h1>👟 ShoeShop — Giày dép &amp; Thời trang</h1>
    <p>Tuyển chọn giày sneaker, giày tây, áo thun, quần jean chính hãng — giá tốt mỗi ngày.</p>
</div>

<div class="row">
    <!-- Cot trai: bo loc -->
    <div class="col-md-3">
        <div class="card filter-card mb-3">
            <div class="card-header fw-bold bg-white">Bộ lọc</div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/home" method="get">
                    <div class="mb-3">
                        <label class="form-label">Danh mục</label>
                        <select name="categoryId" class="form-select">
                            <option value="">-- Tất cả --</option>
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat.id}">${cat.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Giá từ</label>
                        <input type="number" name="minPrice" class="form-control" placeholder="0" min="0">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Giá đến</label>
                        <input type="number" name="maxPrice" class="form-control" placeholder="1000000" min="0">
                    </div>
                    <button type="submit" class="btn btn-primary w-100">Lọc</button>
                </form>
            </div>
        </div>
    </div>

    <!-- Cot phai: danh sach san pham -->
    <div class="col-md-9">
        <h4 class="mb-3 fw-bold">Sản phẩm</h4>
        <div class="row">
            <c:forEach var="p" items="${products}">
                <div class="col-md-4 mb-4">
                    <div class="card product-card h-100">
                        <img src="${pageContext.request.contextPath}/assets/images/${p.image}"
                             class="card-img-top" alt="${p.name}"
                             onerror="this.src='https://via.placeholder.com/300x210?text=No+Image'">
                        <div class="card-body d-flex flex-column">
                            <h6 class="card-title">${p.name}</h6>
                            <p class="product-price mb-2">
                                <fmt:formatNumber value="${p.price}" type="number"/> đ
                            </p>
                            <a href="${pageContext.request.contextPath}/product?id=${p.id}"
                               class="btn btn-outline-primary mt-auto">Xem chi tiết</a>
                        </div>
                    </div>
                </div>
            </c:forEach>

            <c:if test="${empty products}">
                <p class="text-muted">Không có sản phẩm nào.</p>
            </c:if>
        </div>
    </div>
</div>

<%@ include file="common/footer.jsp" %>
