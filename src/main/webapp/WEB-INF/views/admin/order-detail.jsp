<%@ include file="admin-header.jsp" %>
<fmt:setLocale value="vi_VN" />

<style>
    /* Che do in: chi in noi dung don, an thanh dieu huong va cac nut thao tac */
    @media print {
        .admin-navbar, .no-print { display: none !important; }
        body { background: #fff !important; }
        .card { border: 1px solid #ccc !important; box-shadow: none !important; }
    }
</style>

<div class="d-flex justify-content-between align-items-center mb-3 no-print">
    <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-sm btn-outline-secondary">← Danh sách đơn</a>
    <button onclick="window.print()" class="btn btn-sm btn-outline-dark">🖨 In đơn hàng</button>
</div>

<%-- Thong bao sau khi thao tac (truyen qua query string) --%>
<c:if test="${not empty param.msg}">
    <div class="alert alert-success py-2 no-print"><c:out value="${param.msg}"/></div>
</c:if>
<c:if test="${not empty param.err}">
    <div class="alert alert-danger py-2 no-print"><c:out value="${param.err}"/></div>
</c:if>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h3 class="mb-0">Đơn hàng #${order.id}</h3>
    <c:choose>
        <c:when test="${order.status == 'PENDING'}"><span class="badge bg-warning text-dark fs-6">Chờ xử lý</span></c:when>
        <c:when test="${order.status == 'CONFIRMED'}"><span class="badge bg-info text-dark fs-6">Đã xác nhận</span></c:when>
        <c:when test="${order.status == 'SHIPPING'}"><span class="badge bg-primary fs-6">Đang giao</span></c:when>
        <c:when test="${order.status == 'DELIVERED'}"><span class="badge bg-success fs-6">Đã giao</span></c:when>
        <c:when test="${order.status == 'CANCELLED'}"><span class="badge bg-secondary fs-6">Đã hủy</span></c:when>
    </c:choose>
</div>

<div class="row g-4">
    <div class="col-lg-8">
        <%-- ===== Thong tin khach ===== --%>
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-body">
                <h5 class="card-title mb-3">Thông tin người nhận</h5>
                <div class="row">
                    <div class="col-md-6 mb-2"><span class="text-muted">Họ tên:</span> <strong><c:out value="${order.customerName}"/></strong></div>
                    <div class="col-md-6 mb-2"><span class="text-muted">SĐT:</span> <strong><c:out value="${order.phone}"/></strong></div>
                    <div class="col-12 mb-2"><span class="text-muted">Địa chỉ:</span> <c:out value="${order.address}"/></div>
                    <div class="col-md-6 mb-2"><span class="text-muted">Ngày đặt:</span> ${order.createdAt}</div>
                    <c:if test="${not empty order.note}">
                        <div class="col-12"><span class="text-muted">Ghi chú của khách:</span>
                            <em>"<c:out value="${order.note}"/>"</em></div>
                    </c:if>
                </div>
            </div>
        </div>

        <%-- ===== Danh sach mon ===== --%>
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-body">
                <h5 class="card-title mb-3">Sản phẩm trong đơn</h5>
                <table class="table align-middle mb-0">
                    <thead class="table-light">
                        <tr>
                            <th>Sản phẩm</th><th>Phân loại</th>
                            <th class="text-center">SL</th>
                            <th class="text-end">Đơn giá</th>
                            <th class="text-end">Thành tiền</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${order.items}">
                            <tr>
                                <td>
                                    <img src="${pageContext.request.contextPath}/assets/images/${item.productImage}"
                                         alt="" style="width:42px;height:42px;object-fit:cover;border-radius:6px;" class="me-2">
                                    <c:out value="${item.productName}"/>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty item.variantName}"><c:out value="${item.variantName}"/></c:when>
                                        <c:otherwise><span class="text-muted">—</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-center">${item.quantity}</td>
                                <td class="text-end"><fmt:formatNumber value="${item.price}" type="number" maxFractionDigits="0"/> đ</td>
                                <td class="text-end fw-bold"><fmt:formatNumber value="${item.subtotal}" type="number" maxFractionDigits="0"/> đ</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                    <tfoot>
                        <tr>
                            <td colspan="4" class="text-end text-muted">Tạm tính:</td>
                            <td class="text-end"><fmt:formatNumber value="${order.subtotal}" type="number" maxFractionDigits="0"/> đ</td>
                        </tr>
                        <c:if test="${order.discountAmount > 0}">
                            <tr>
                                <td colspan="4" class="text-end text-muted">
                                    Giảm giá<c:if test="${not empty order.couponCode}"> (mã <c:out value="${order.couponCode}"/>)</c:if>:
                                </td>
                                <td class="text-end text-success">-<fmt:formatNumber value="${order.discountAmount}" type="number" maxFractionDigits="0"/> đ</td>
                            </tr>
                        </c:if>
                        <tr>
                            <td colspan="4" class="text-end text-muted">Phí giao hàng:</td>
                            <td class="text-end">
                                <c:choose>
                                    <c:when test="${order.shipFee > 0}"><fmt:formatNumber value="${order.shipFee}" type="number" maxFractionDigits="0"/> đ</c:when>
                                    <c:otherwise>Miễn phí</c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <tr class="table-light">
                            <td colspan="4" class="text-end fw-bold">Tổng cộng:</td>
                            <td class="text-end fw-bold text-danger fs-5"><fmt:formatNumber value="${order.totalAmount}" type="number" maxFractionDigits="0"/> đ</td>
                        </tr>
                    </tfoot>
                </table>
            </div>
        </div>
    </div>

    <div class="col-lg-4">
        <%-- ===== Xu ly don ===== --%>
        <div class="card shadow-sm border-0 mb-4 no-print">
            <div class="card-body">
                <h5 class="card-title mb-3">Xử lý đơn</h5>
                <c:choose>
                    <c:when test="${not empty nextStatuses}">
                        <form action="${pageContext.request.contextPath}/admin/orders" method="post" class="d-flex gap-2">
                            <input type="hidden" name="action" value="status">
                            <input type="hidden" name="id" value="${order.id}">
                            <select name="status" class="form-select form-select-sm">
                                <c:forEach var="s" items="${nextStatuses}">
                                    <option value="${s}">
                                        <c:choose>
                                            <c:when test="${s == 'CONFIRMED'}">→ Xác nhận đơn</c:when>
                                            <c:when test="${s == 'SHIPPING'}">→ Bắt đầu giao</c:when>
                                            <c:when test="${s == 'DELIVERED'}">→ Đã giao xong</c:when>
                                            <c:when test="${s == 'CANCELLED'}">✕ Hủy đơn (hoàn kho)</c:when>
                                            <c:otherwise>${s}</c:otherwise>
                                        </c:choose>
                                    </option>
                                </c:forEach>
                            </select>
                            <button type="submit" class="btn btn-sm btn-primary flex-shrink-0">Cập nhật</button>
                        </form>
                        <p class="small text-muted mt-2 mb-0">Hủy đơn sẽ tự động hoàn lại tồn kho các sản phẩm trong đơn.</p>
                    </c:when>
                    <c:otherwise>
                        <p class="text-muted mb-0">Đơn đã ở trạng thái cuối, không thể thay đổi.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <%-- ===== Ghi chu noi bo ===== --%>
        <div class="card shadow-sm border-0 mb-4 no-print">
            <div class="card-body">
                <h5 class="card-title mb-3">Ghi chú nội bộ</h5>
                <form action="${pageContext.request.contextPath}/admin/orders" method="post">
                    <input type="hidden" name="action" value="note">
                    <input type="hidden" name="id" value="${order.id}">
                    <textarea name="adminNote" class="form-control form-control-sm mb-2" rows="3" maxlength="500"
                              placeholder="Ví dụ: đã gọi khách 2 lần chưa nghe máy..."><c:out value="${order.adminNote}"/></textarea>
                    <button type="submit" class="btn btn-sm btn-outline-primary w-100">Lưu ghi chú</button>
                </form>
            </div>
        </div>

        <%-- ===== Lich su trang thai ===== --%>
        <div class="card shadow-sm border-0">
            <div class="card-body">
                <h5 class="card-title mb-3">Lịch sử đơn hàng</h5>
                <c:choose>
                    <c:when test="${not empty order.history}">
                        <ul class="list-unstyled mb-0">
                            <c:forEach var="h" items="${order.history}">
                                <li class="d-flex mb-3">
                                    <span class="me-2">
                                        <c:choose>
                                            <c:when test="${h.newStatus == 'CANCELLED'}">🔴</c:when>
                                            <c:when test="${h.newStatus == 'DELIVERED'}">🟢</c:when>
                                            <c:otherwise>🔵</c:otherwise>
                                        </c:choose>
                                    </span>
                                    <div>
                                        <div class="fw-semibold">${h.newStatusLabel}</div>
                                        <small class="text-muted">${h.createdAt} ·
                                            <c:choose>
                                                <c:when test="${h.changedBy == 'customer'}">khách đặt hàng</c:when>
                                                <c:otherwise>bởi <c:out value="${h.changedBy}"/></c:otherwise>
                                            </c:choose>
                                        </small>
                                    </div>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:when>
                    <c:otherwise>
                        <p class="text-muted mb-0">Chưa có lịch sử (đơn tạo trước khi có tính năng này).</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<%@ include file="admin-footer.jsp" %>
