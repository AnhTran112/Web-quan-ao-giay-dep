<%@ include file="common/header.jsp" %>
<fmt:setLocale value="vi_VN" />

<%-- Chi tiet san pham. Du lieu: request attribute "product", "categoryName" --%>

<!-- Breadcrumb kieu Shopee: Trang chu > Danh muc > Ten san pham -->
<nav aria-label="breadcrumb" class="shopee-crumb my-2">
    <a href="${pageContext.request.contextPath}/home">Trang chủ</a>
    <span class="crumb-sep">›</span>
    <a href="${pageContext.request.contextPath}/home?categoryId=${product.categoryId}">
        ${not empty categoryName ? categoryName : 'Sản phẩm'}</a>
    <span class="crumb-sep">›</span>
    <span class="text-muted"><c:out value="${product.name}"/></span>
</nav>

<!-- Khoi thong tin chinh: anh ben trai, thong tin + mua hang ben phai -->
<div class="card border-0 shadow-sm p-3 mb-4">
    <div class="row g-4">
        <div class="col-12 col-lg-5">
            <div class="detail-img-wrap mb-2">
                <img id="mainProductImage" src="${pageContext.request.contextPath}/assets/images/${product.image}"
                     alt="<c:out value='${product.name}'/>"
                     onerror="this.src='https://via.placeholder.com/400x400?text=No+Image'">
            </div>
            <%-- Gallery --%>
            <c:if test="${not empty images}">
                <div class="d-flex gap-2 overflow-auto py-2">
                    <img src="${pageContext.request.contextPath}/assets/images/${product.image}"
                         class="img-thumbnail cursor-pointer" width="60" height="60"
                         onclick="document.getElementById('mainProductImage').src=this.src" style="cursor: pointer; object-fit: cover;">
                    <c:forEach var="img" items="${images}">
                        <img src="${pageContext.request.contextPath}/assets/images/${img.imageUrl}"
                             class="img-thumbnail cursor-pointer" width="60" height="60"
                             onclick="document.getElementById('mainProductImage').src=this.src" style="cursor: pointer; object-fit: cover;">
                    </c:forEach>
                </div>
            </c:if>
        </div>
        <div class="col-12 col-lg-7">
            <h3 class="fw-bold mb-1"><c:out value="${product.name}"/></h3>

            <div class="rating-row">
                ${not empty categoryName ? categoryName : 'Sản phẩm'}
                &nbsp;|&nbsp;
                <c:choose>
                    <c:when test="${product.quantity > 0}">Còn hàng</c:when>
                    <c:otherwise>Tạm hết hàng</c:otherwise>
                </c:choose>
            </div>

            <div class="detail-price-box">
                <c:choose>
                    <c:when test="${product.hasVariants}">
                        <span class="price" id="priceNow">
                            <fmt:formatNumber value="${product.minPrice * (100 - product.discountPercent) / 100}" type="number" maxFractionDigits="0"/>
                            <c:if test="${product.minPrice != product.maxPrice}">
                                – <fmt:formatNumber value="${product.maxPrice * (100 - product.discountPercent) / 100}" type="number" maxFractionDigits="0"/>
                            </c:if> đ
                        </span>
                    </c:when>
                    <c:otherwise>
                        <span class="price" id="priceNow">
                            <fmt:formatNumber value="${product.price * (100 - product.discountPercent) / 100}" type="number" maxFractionDigits="0"/> đ
                        </span>
                    </c:otherwise>
                </c:choose>
                <c:if test="${product.discountPercent > 0}">
                    <span class="price-old"><fmt:formatNumber value="${product.hasVariants ? product.minPrice : product.price}" type="number" maxFractionDigits="0"/> đ</span>
                    <span class="discount-badge">-${product.discountPercent}%</span>
                </c:if>
            </div>

            <%-- Nut chon phan loai (chi hien khi san pham co variants) --%>
            <c:if test="${product.hasVariants}">
                <div class="mb-3">
                    <label class="form-label fw-semibold">Phân loại</label>
                    <div class="d-flex flex-wrap gap-2" id="variantGroup">
                        <c:forEach var="v" items="${product.variants}">
                            <button type="button" class="btn btn-outline-secondary variant-btn"
                                    data-id="${v.id}"
                                    data-price="${v.price * (100 - product.discountPercent) / 100}"
                                    data-stock="${v.quantity}"><c:out value="${v.name}"/></button>
                        </c:forEach>
                    </div>
                </div>
            </c:if>

            <%-- Form them vao gio hang voi bo tang/giam so luong --%>
            <form id="addToCartForm" onsubmit="event.preventDefault(); submitAddToCart();">
                <input type="hidden" name="productId" id="productId" value="${product.id}">
                <input type="hidden" name="variantId" id="variantId" value="">

                <label class="form-label fw-semibold">Số lượng</label>
                <div class="input-group mb-3" style="max-width: 160px;">
                    <button type="button" class="btn btn-outline-secondary" onclick="changeQty(-1)">−</button>
                    <input type="number" id="qty" name="quantity" value="1" min="1"
                           max="${product.quantity}" class="form-control text-center">
                    <button type="button" class="btn btn-outline-secondary" onclick="changeQty(1)">+</button>
                </div>

                <div class="d-flex gap-2 flex-wrap product-actions">
                    <button type="submit" class="btn btn-success btn-lg"
                            <c:if test="${product.quantity == 0}">disabled</c:if>>
                        Thêm vào giỏ
                    </button>
                    <button type="button" class="btn btn-buy-now btn-lg" onclick="buyNow()"
                            <c:if test="${product.quantity == 0}">disabled</c:if>>
                        Mua ngay
                    </button>
                    <button type="button" class="btn btn-outline-danger btn-lg" onclick="toggleWishlist(${product.id})">
                        <i class="bi bi-heart"></i> Yêu thích
                    </button>
                </div>
            </form>

            <%-- Dai cam ket dich vu, tao cam giac chuyen nghiep --%>
            <div class="commit-strip">
                <span>Giao hàng toàn quốc</span>
                <span>Đổi trả trong 7 ngày</span>
                <span>Cam kết hàng chính hãng</span>
            </div>
        </div>
    </div>
</div>

<!-- Mo ta san pham (kieu Shopee: mot khu rieng ben duoi) -->
<h5 class="section-title">Mô tả sản phẩm</h5>
<div class="desc-box mb-4"><c:choose><c:when test="${not empty product.description}"><c:out value="${product.description}" escapeXml="false"/></c:when><c:otherwise>Sản phẩm chính hãng, chất lượng đảm bảo. Hiện chưa có mô tả chi tiết cho sản phẩm này.</c:otherwise></c:choose></div>

<!-- Bang thong so -->
<h5 class="section-title">Thông tin chi tiết</h5>
<div class="card border-0 shadow-sm mb-4">
    <table class="table table-striped mb-0 spec-table">
        <tbody>
            <tr><th>Mã sản phẩm</th><td>#${product.id}</td></tr>
            <tr><th>Danh mục</th><td>${not empty categoryName ? categoryName : 'Đang cập nhật'}</td></tr>
            <tr><th>Giá bán</th><td><fmt:formatNumber value="${product.price}" type="number"/> đ</td></tr>
            <tr><th>Tồn kho</th><td>${product.quantity} sản phẩm</td></tr>
        </tbody>
    </table>
</div>

<!-- Đánh giá sản phẩm -->
<h5 class="section-title">Đánh giá sản phẩm <c:if test="${not empty avgRating}"><span class="badge bg-warning text-dark"><i class="bi bi-star-fill"></i> <fmt:formatNumber value="${avgRating}" maxFractionDigits="1"/> / 5</span></c:if></h5>
<div class="card border-0 shadow-sm mb-4">
    <div class="card-body">
        <div class="row">
            <div class="col-md-6 border-end">
                <h6 class="fw-bold mb-3">Gửi đánh giá của bạn</h6>
                <form id="reviewForm" onsubmit="event.preventDefault(); submitReview();">
                    <input type="hidden" id="revProductId" value="${product.id}">
                    <div class="mb-2">
                        <label class="form-label small">Số điện thoại (đã dùng để mua hàng):</label>
                        <input type="text" id="revPhone" class="form-control form-control-sm" required>
                    </div>
                    <div class="mb-2">
                        <label class="form-label small">Đánh giá (sao):</label>
                        <select id="revRating" class="form-select form-select-sm">
                            <option value="5">5 Sao (Rất tốt)</option>
                            <option value="4">4 Sao (Tốt)</option>
                            <option value="3">3 Sao (Bình thường)</option>
                            <option value="2">2 Sao (Kém)</option>
                            <option value="1">1 Sao (Rất kém)</option>
                        </select>
                    </div>
                    <div class="mb-2">
                        <label class="form-label small">Bình luận:</label>
                        <textarea id="revComment" class="form-control form-control-sm" rows="3" required></textarea>
                    </div>
                    <button type="submit" class="btn btn-sm btn-primary">Gửi đánh giá</button>
                </form>
            </div>
            <div class="col-md-6">
                <h6 class="fw-bold mb-3">Các đánh giá gần đây</h6>
                <div class="review-list" style="max-height: 300px; overflow-y: auto;">
                    <c:choose>
                        <c:when test="${empty reviews}">
                            <p class="text-muted small">Chưa có đánh giá nào.</p>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="rev" items="${reviews}">
                                <div class="border-bottom mb-2 pb-2">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <strong>Khách hàng: ${rev.phone.replaceAll(".{3}$", "***")}</strong>
                                        <span class="text-warning small">
                                            <c:forEach begin="1" end="${rev.rating}">★</c:forEach>
                                        </span>
                                    </div>
                                    <div class="small text-muted mb-1"><fmt:formatDate value="${rev.createdAt}" pattern="dd/MM/yyyy HH:mm"/></div>
                                    <p class="mb-0 small"><c:out value="${rev.comment}"/></p>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Sản phẩm liên quan -->
<c:if test="${not empty relatedProducts}">
    <h5 class="section-title mt-4">Sản phẩm liên quan</h5>
    <div class="row row-cols-2 row-cols-md-4 g-3 mb-4">
        <c:forEach var="rp" items="${relatedProducts}">
            <div class="col">
                <div class="card h-100">
                    <a href="${pageContext.request.contextPath}/product?id=${rp.id}"><img src="${pageContext.request.contextPath}/assets/images/${rp.image}" class="card-img-top" style="object-fit: cover; height: 150px;"></a>
                    <div class="card-body p-2 text-center">
                        <a href="${pageContext.request.contextPath}/product?id=${rp.id}" class="text-decoration-none text-dark d-block text-truncate small"><c:out value="${rp.name}"/></a>
                        <strong class="text-danger small"><fmt:formatNumber value="${rp.price}" type="number"/>đ</strong>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</c:if>

<!-- Đã xem gần đây -->
<c:if test="${not empty recentlyViewed}">
    <h5 class="section-title mt-4">Sản phẩm vừa xem</h5>
    <div class="d-flex gap-3 overflow-auto pb-2 mb-4">
        <c:forEach var="rv" items="${recentlyViewed}">
            <div class="card flex-shrink-0" style="width: 120px;">
                <a href="${pageContext.request.contextPath}/product?id=${rv.id}"><img src="${pageContext.request.contextPath}/assets/images/${rv.image}" class="card-img-top" style="object-fit: cover; height: 120px;"></a>
                <div class="card-body p-1 text-center">
                    <a href="${pageContext.request.contextPath}/product?id=${rv.id}" class="text-decoration-none text-dark d-block text-truncate" style="font-size: 0.8rem;"><c:out value="${rv.name}"/></a>
                </div>
            </div>
        </c:forEach>
    </div>
</c:if>

<script>
    // Tang/giam so luong, khong cho xuong duoi 1
    function changeQty(delta) {
        var input = document.getElementById('qty');
        var value = parseInt(input.value) || 1;
        value += delta;
        if (value < 1) value = 1;
        input.value = value;
    }

    // Chon phan loai: doi gia + ton kho + gioi han so luong theo loai dang chon
    document.querySelectorAll('.variant-btn').forEach(function (btn) {
        btn.addEventListener('click', function () {
            document.querySelectorAll('.variant-btn').forEach(function (b) {
                b.classList.remove('active', 'btn-primary');
                b.classList.add('btn-outline-secondary');
            });
            btn.classList.add('active', 'btn-primary');
            btn.classList.remove('btn-outline-secondary');
            document.getElementById('variantId').value = btn.dataset.id;
            var price = Number(btn.dataset.price);
            document.getElementById('priceNow').innerText = price.toLocaleString('vi-VN') + ' đ';
            var qty = document.getElementById('qty');
            qty.max = btn.dataset.stock;
            if (Number(qty.value) > Number(btn.dataset.stock)) qty.value = btn.dataset.stock;
        });
    });

    // Handle Add To Cart
    function submitAddToCart() {
        if (${product.hasVariants} === true && document.getElementById('variantId').value === '') {
            alert('Vui lòng chọn phân loại');
            return;
        }
        let pId = document.getElementById('productId').value;
        let vId = document.getElementById('variantId').value;
        let qty = document.getElementById('qty').value;
        addToCartAjax(pId, qty, vId);
    }

    function buyNow() {
        if (${product.hasVariants} === true && document.getElementById('variantId').value === '') {
            alert('Vui lòng chọn phân loại');
            return;
        }
        let pId = document.getElementById('productId').value;
        let vId = document.getElementById('variantId').value;
        let qty = document.getElementById('qty').value;
        
        // Them vao gio roi redirect
        fetch('${pageContext.request.contextPath}/api/cart/add', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'productId=' + pId + '&variantId=' + vId + '&quantity=' + qty
        }).then(() => {
            window.location.href = '${pageContext.request.contextPath}/checkout';
        });
    }

    // Handle Submit Review
    function submitReview() {
        let pId = document.getElementById('revProductId').value;
        let phone = document.getElementById('revPhone').value;
        let rating = document.getElementById('revRating').value;
        let comment = document.getElementById('revComment').value;
        
        fetch('${pageContext.request.contextPath}/api/review', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'productId=' + pId + '&phone=' + encodeURIComponent(phone) + '&rating=' + rating + '&comment=' + encodeURIComponent(comment)
        })
        .then(res => res.json())
        .then(data => {
            if(data.success) {
                alert(data.message);
                window.location.reload();
            } else {
                alert(data.message);
            }
        });
    }
</script>

<%@ include file="common/footer.jsp" %>
