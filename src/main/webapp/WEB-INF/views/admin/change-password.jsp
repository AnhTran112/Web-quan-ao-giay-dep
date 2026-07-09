<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="admin-header.jsp" %>

<div class="row justify-content-center">
    <div class="col-md-6">
        <div class="card shadow-sm mt-4">
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0">Đổi mật khẩu</h5>
            </div>
            <div class="card-body">
                <c:if test="${not empty error}">
                    <div class="alert alert-danger"><c:out value="${error}"/></div>
                </c:if>
                <c:if test="${not empty success}">
                    <div class="alert alert-success"><c:out value="${success}"/></div>
                </c:if>

                <form action="${postUrl}" method="post">
                    <div class="mb-3">
                        <label class="form-label">Mật khẩu cũ</label>
                        <input type="password" name="oldPassword" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Mật khẩu mới</label>
                        <input type="password" name="newPassword" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Xác nhận mật khẩu mới</label>
                        <input type="password" name="confirmPassword" class="form-control" required>
                    </div>
                    <button type="submit" class="btn btn-primary w-100">Đổi mật khẩu</button>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="admin-footer.jsp" %>
