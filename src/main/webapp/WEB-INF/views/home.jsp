<%@ include file="common/header.jsp" %>

<div class="row">
    <!-- Cot trai: bo loc -->
    <div class="col-md-3">
        <div class="card mb-3">
            <div class="card-header fw-bold">Bộ lọc</div>
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
                        <input type="number" name="minPrice" class="form-control" placeholder="0">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Giá đến</label>
                        <input type="number" name="maxPrice" class="form-control" placeholder="1000000">
                    </div>
                    <button type="submit" class="btn btn-primary w-100">Lọc</button>
                </form>
            </div>
        </div>
    </div>

    <!-- Cot phai: danh sach san pham -->
    <div class="col-md-9">
        <h3 class="mb-3">Sản phẩm</h3>
        <div class="row">
            <c:forEach var="p" items="${products}">
                <div class="col-md-4 mb-4">
                    <div class="card h-100">
                        <img src="${pageContext.request.contextPath}/assets/images/${p.image}"
                             class="card-img-top" alt="${p.name}"
                             onerror="this.src='https://via.placeholder.com/300x200?text=No+Image'">
                        <div class="card-body d-flex flex-column">
                            <h6 class="card-title">${p.name}</h6>
                            <p class="text-danger fw-bold">
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
        
        <!-- Phan trang -->
        <c:if test="${totalPages > 1}">
            <nav aria-label="Page navigation">
                <ul class="pagination justify-content-center mt-4">
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <c:url var="pageUrl" value="/home">
                            <c:param name="page" value="${i}" />
                            <c:if test="${not empty param.categoryId}"><c:param name="categoryId" value="${param.categoryId}" /></c:if>
                            <c:if test="${not empty param.minPrice}"><c:param name="minPrice" value="${param.minPrice}" /></c:if>
                            <c:if test="${not empty param.maxPrice}"><c:param name="maxPrice" value="${param.maxPrice}" /></c:if>
                        </c:url>
                        <li class="page-item ${currentPage == i ? 'active' : ''}">
                            <a class="page-link" href="${pageUrl}">${i}</a>
                        </li>
                    </c:forEach>
                </ul>
            </nav>
        </c:if>
    </div>
</div>

<%@ include file="common/footer.jsp" %>
