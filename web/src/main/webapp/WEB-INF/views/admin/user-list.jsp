<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="admin-header.jsp" %>

<div class="d-flex justify-content-between mb-3">
    <h3>Quản lý Tài khoản (Chỉ ADMIN)</h3>
    <button class="btn btn-success" data-bs-toggle="modal" data-bs-target="#addUserModal">+ Tạo Tài khoản</button>
</div>

<c:if test="${not empty param.msg}">
    <div class="alert alert-success">Cập nhật thành công!</div>
</c:if>
<c:if test="${not empty param.error}">
    <div class="alert alert-danger">Có lỗi xảy ra (Tài khoản có thể đã tồn tại)!</div>
</c:if>

<div class="table-responsive">
    <table class="table table-bordered table-hover align-middle">
        <thead class="table-light">
        <tr>
            <th>ID</th>
            <th>Tài khoản</th>
            <th>Họ và tên</th>
            <th>Vai trò</th>
            <th>Trạng thái</th>
            <th>Lỗi đ.nhập</th>
            <th>Hành động</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="u" items="${users}">
            <tr>
                <td>${u.id}</td>
                <td><strong class="text-primary"><c:out value="${u.username}"/></strong></td>
                <td><c:out value="${u.fullName}"/></td>
                
                <form action="${pageContext.request.contextPath}/admin/users" method="post" style="display:inline;">
                    <input type="hidden" name="action" value="updateRoleStatus">
                    <input type="hidden" name="id" value="${u.id}">
                    <td>
                        <select name="role" class="form-select form-select-sm">
                            <option value="CUSTOMER" ${u.role == 'CUSTOMER' ? 'selected' : ''}>CUSTOMER (Khách)</option>
                            <option value="STAFF" ${u.role == 'STAFF' ? 'selected' : ''}>STAFF (Nhân viên)</option>
                            <option value="ADMIN" ${u.role == 'ADMIN' ? 'selected' : ''}>ADMIN (Quản trị)</option>
                        </select>
                    </td>
                    <td>
                        <select name="status" class="form-select form-select-sm ${u.status == 'LOCKED' ? 'border-danger text-danger' : ''}">
                            <option value="ACTIVE" ${u.status == 'ACTIVE' ? 'selected' : ''}>Hoạt động</option>
                            <option value="LOCKED" ${u.status == 'LOCKED' ? 'selected' : ''}>Khóa</option>
                        </select>
                        <c:if test="${not empty u.lockTime}">
                            <div class="text-danger small"><fmt:formatDate value="${u.lockTime}" pattern="HH:mm dd/MM" /></div>
                        </c:if>
                    </td>
                    <td>
                        <span class="badge bg-secondary">${u.failedAttempts} lần</span>
                    </td>
                    <td>
                        <button type="submit" class="btn btn-sm btn-primary">Lưu</button>
                        <c:if test="${u.role != 'ADMIN' || loggedInUser.username != u.username}">
                            <a href="${pageContext.request.contextPath}/admin/users?action=delete&id=${u.id}"
                               class="btn btn-sm btn-danger ms-1" onclick="return confirm('Bạn có chắc muốn xóa tài khoản này?');">Xóa</a>
                        </c:if>
                    </td>
                </form>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<!-- Modal Thêm Tài Khoản -->
<div class="modal fade" id="addUserModal" tabindex="-1">
    <div class="modal-dialog">
        <form action="${pageContext.request.contextPath}/admin/users" method="post" class="modal-content">
            <input type="hidden" name="action" value="add">
            <div class="modal-header">
                <h5 class="modal-title">Tạo Tài Khoản Mới</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="mb-3">
                    <label class="form-label">Tên đăng nhập</label>
                    <input type="text" name="username" class="form-control" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Mật khẩu</label>
                    <input type="password" name="password" class="form-control" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Họ và tên</label>
                    <input type="text" name="fullName" class="form-control" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Phân quyền</label>
                    <select name="role" class="form-select">
                        <option value="STAFF">STAFF (Nhân viên)</option>
                        <option value="ADMIN">ADMIN (Quản trị)</option>
                        <option value="CUSTOMER">CUSTOMER (Khách hàng)</option>
                    </select>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                <button type="submit" class="btn btn-primary">Tạo tài khoản</button>
            </div>
        </form>
    </div>
</div>

<%@ include file="admin-footer.jsp" %>
