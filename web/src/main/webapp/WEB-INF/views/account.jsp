<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="common/header.jsp" %>
<fmt:setLocale value="vi_VN" />

<div class="row">
    <!-- Sidebar / Tabs Menu -->
    <div class="col-md-3 mb-4">
        <div class="list-group" id="account-tabs" role="tablist">
            <a class="list-group-item list-group-item-action ${empty param.tab || param.tab == 'profile' ? 'active' : ''}" 
               id="profile-tab" data-bs-toggle="list" href="#profile" role="tab">
               <i class="bi bi-person-circle me-2"></i>Thông tin tài khoản
            </a>
            <a class="list-group-item list-group-item-action ${param.tab == 'password' ? 'active' : ''}" 
               id="password-tab" data-bs-toggle="list" href="#password" role="tab">
               <i class="bi bi-key-fill me-2"></i>Đổi mật khẩu
            </a>
            <a class="list-group-item list-group-item-action ${param.tab == 'orders' ? 'active' : ''}" 
               id="orders-tab" data-bs-toggle="list" href="#orders" role="tab">
               <i class="bi bi-box-seam me-2"></i>Lịch sử mua hàng
            </a>
        </div>
    </div>

    <!-- Tab Content -->
    <div class="col-md-9">
        <div class="tab-content" id="nav-tabContent">
            
            <!-- TAB 1: THONG TIN TAI KHOAN -->
            <div class="tab-pane fade ${empty param.tab || param.tab == 'profile' ? 'show active' : ''}" id="profile" role="tabpanel">
                <div class="card shadow-sm border-0">
                    <div class="card-header bg-white border-bottom-0 pt-4 pb-0">
                        <h5 class="mb-0">Hồ Sơ Của Tôi</h5>
                        <p class="text-muted small">Quản lý thông tin hồ sơ để bảo mật tài khoản</p>
                    </div>
                    <div class="card-body">
                        <c:if test="${param.success == 'ProfileUpdated'}">
                            <div class="alert alert-success">Cập nhật hồ sơ thành công!</div>
                        </c:if>
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger"><c:out value="${error}"/></div>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/account" method="post">
                            <div class="row mb-3 align-items-center">
                                <div class="col-md-3 text-md-end"><label class="form-label mb-0">Tên đăng nhập</label></div>
                                <div class="col-md-7">
                                    <input type="text" class="form-control-plaintext fw-bold" value="<c:out value='${sessionScope.loggedInUser.username}'/>" readonly>
                                </div>
                            </div>
                            <div class="row mb-3 align-items-center">
                                <div class="col-md-3 text-md-end"><label class="form-label mb-0">Họ và tên</label></div>
                                <div class="col-md-7">
                                    <input type="text" name="fullName" class="form-control" required value="<c:out value='${sessionScope.loggedInUser.fullName}'/>">
                                </div>
                            </div>
                            <div class="row mb-3 align-items-center">
                                <div class="col-md-3 text-md-end"><label class="form-label mb-0">Số điện thoại</label></div>
                                <div class="col-md-7">
                                    <input type="text" name="phone" class="form-control" value="<c:out value='${sessionScope.loggedInUser.phone}'/>">
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-3 text-md-end"><label class="form-label mb-0 mt-2">Địa chỉ</label></div>
                                <div class="col-md-7">
                                    <textarea name="address" class="form-control" rows="2"><c:out value='${sessionScope.loggedInUser.address}'/></textarea>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-3"></div>
                                <div class="col-md-7">
                                    <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <!-- TAB 2: DOI MAT KHAU -->
            <div class="tab-pane fade ${param.tab == 'password' ? 'show active' : ''}" id="password" role="tabpanel">
                <div class="card shadow-sm border-0">
                    <div class="card-header bg-white border-bottom-0 pt-4 pb-0">
                        <h5 class="mb-0">Đổi Mật Khẩu</h5>
                        <p class="text-muted small">Để bảo mật tài khoản, vui lòng không chia sẻ mật khẩu cho người khác</p>
                    </div>
                    <div class="card-body">
                        <c:if test="${param.success == 'PasswordChanged'}">
                            <div class="alert alert-success">Đổi mật khẩu thành công!</div>
                        </c:if>
                        <c:if test="${param.error == 'PasswordMismatch'}">
                            <div class="alert alert-danger">Mật khẩu xác nhận không khớp!</div>
                        </c:if>
                        <c:if test="${param.error == 'UpdateFailed'}">
                            <div class="alert alert-danger">Lỗi hệ thống. Đổi mật khẩu thất bại!</div>
                        </c:if>
                        <c:if test="${param.error == 'WrongOldPassword'}">
                            <div class="alert alert-danger">Mật khẩu cũ không chính xác!</div>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/change-password" method="post">
                            <div class="row mb-3 align-items-center">
                                <div class="col-md-3 text-md-end"><label class="form-label mb-0">Mật khẩu cũ</label></div>
                                <div class="col-md-7">
                                    <input type="password" name="oldPassword" class="form-control" required>
                                </div>
                            </div>
                            <div class="row mb-3 align-items-center">
                                <div class="col-md-3 text-md-end"><label class="form-label mb-0">Mật khẩu mới</label></div>
                                <div class="col-md-7">
                                    <input type="password" name="newPassword" class="form-control" required>
                                </div>
                            </div>
                            <div class="row mb-3 align-items-center">
                                <div class="col-md-3 text-md-end"><label class="form-label mb-0">Xác nhận mật khẩu</label></div>
                                <div class="col-md-7">
                                    <input type="password" name="confirmPassword" class="form-control" required>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-3"></div>
                                <div class="col-md-7">
                                    <button type="submit" class="btn btn-primary">Xác nhận</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <!-- TAB 3: LICH SU MUA HANG -->
            <div class="tab-pane fade ${param.tab == 'orders' ? 'show active' : ''}" id="orders" role="tabpanel">
                <c:choose>
                    <c:when test="${empty orders}">
                        <div class="alert alert-info">
                            Bạn chưa có đơn hàng nào. <a href="${pageContext.request.contextPath}/home">Tiếp tục mua sắm</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <h5 class="mb-3">Bạn có ${orders.size()} đơn hàng</h5>
                        <c:forEach var="o" items="${orders}">
                            <div class="card shadow-sm border-0 mb-3">
                                <div class="card-body">
                                    <div class="d-flex justify-content-between align-items-center mb-2 flex-wrap gap-2">
                                        <div>
                                            <span class="fw-bold">Đơn #${o.id}</span>
                                            <span class="text-muted small ms-2">Đặt ngày: ${o.createdAt}</span>
                                        </div>
                                        <c:choose>
                                            <c:when test="${o.status == 'PENDING'}"><span class="badge bg-warning text-dark">Chờ xử lý</span></c:when>
                                            <c:when test="${o.status == 'CONFIRMED'}"><span class="badge bg-info text-dark">Đã xác nhận</span></c:when>
                                            <c:when test="${o.status == 'SHIPPING'}"><span class="badge bg-primary">Đang giao</span></c:when>
                                            <c:when test="${o.status == 'DELIVERED'}"><span class="badge bg-success">Đã giao</span></c:when>
                                            <c:when test="${o.status == 'CANCELLED'}"><span class="badge bg-secondary">Đã hủy</span></c:when>
                                        </c:choose>
                                    </div>
                                    <div class="list-group list-group-flush border-top border-bottom mb-3">
                                        <c:forEach var="item" items="${o.items}">
                                            <div class="list-group-item px-0 py-2 border-0 border-bottom d-flex align-items-center">
                                                <c:choose>
                                                    <c:when test="${not empty item.productImage}">
                                                        <img src="${pageContext.request.contextPath}/assets/images/${item.productImage}" class="rounded border me-3" style="width: 60px; height: 60px; object-fit: cover;" alt="<c:out value='${item.productName}'/>" onerror="this.src='https://via.placeholder.com/60x60?text=No+Image'">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="rounded border me-3 bg-light d-flex align-items-center justify-content-center text-muted" style="width: 60px; height: 60px;">
                                                            <i class="bi bi-image"></i>
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>
                                                <div class="flex-grow-1">
                                                    <div class="mb-1 text-dark fw-bold" style="font-size: 0.95rem;"><c:out value="${item.productName}"/></div>
                                                    <div class="text-muted" style="font-size: 0.85rem;">
                                                        <c:if test="${not empty item.variantName}">
                                                            Phân loại: <span class="fw-semibold"><c:out value="${item.variantName}"/></span> |
                                                        </c:if>
                                                        Số lượng: x${item.quantity}
                                                    </div>
                                                </div>
                                                <div class="text-end fw-bold text-danger">
                                                    <fmt:formatNumber value="${item.subtotal}" type="number" maxFractionDigits="0"/> đ
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                    <div class="mt-2 mb-3">
                                        <button class="btn btn-sm btn-outline-secondary" type="button" data-bs-toggle="collapse" data-bs-target="#history-${o.id}">
                                            <i class="bi bi-clock-history"></i> Lịch sử trạng thái đơn
                                        </button>
                                        <div class="collapse mt-2" id="history-${o.id}">
                                            <ul class="list-group list-group-flush small border rounded">
                                                <c:forEach var="h" items="${o.history}">
                                                    <li class="list-group-item py-2">
                                                        <c:choose>
                                                            <c:when test="${h.newStatus == 'DELIVERED'}"><i class="bi bi-check-circle-fill text-success me-2"></i></c:when>
                                                            <c:when test="${h.newStatus == 'CANCELLED'}"><i class="bi bi-x-circle-fill text-danger me-2"></i></c:when>
                                                            <c:otherwise><i class="bi bi-record-circle text-primary me-2"></i></c:otherwise>
                                                        </c:choose>
                                                        <span class="fw-bold">
                                                            <c:choose>
                                                                <c:when test="${h.newStatus == 'PENDING'}">Chờ xử lý</c:when>
                                                                <c:when test="${h.newStatus == 'CONFIRMED'}">Đã xác nhận</c:when>
                                                                <c:when test="${h.newStatus == 'SHIPPING'}">Đang giao</c:when>
                                                                <c:when test="${h.newStatus == 'DELIVERED'}">Đã giao</c:when>
                                                                <c:when test="${h.newStatus == 'CANCELLED'}">Đã hủy</c:when>
                                                            </c:choose>
                                                        </span>
                                                        <br>
                                                        <span class="text-muted ms-4">${h.createdAt}</span>
                                                        <span class="text-muted fst-italic">
                                                            <c:choose>
                                                                <c:when test="${h.changedBy == 'customer'}"> · khách đặt hàng</c:when>
                                                                <c:otherwise> · bởi admin</c:otherwise>
                                                            </c:choose>
                                                        </span>
                                                    </li>
                                                </c:forEach>
                                            </ul>
                                        </div>
                                    </div>

                                    <div class="d-flex justify-content-between align-items-center mt-3">
                                        <small class="text-muted">Giao đến: <c:out value="${o.address}"/></small>
                                        <span class="fw-bold text-danger fs-5"><fmt:formatNumber value="${o.totalAmount}" type="number" maxFractionDigits="0"/> đ</span>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
            
        </div>
    </div>
</div>

<%@ include file="common/footer.jsp" %>
