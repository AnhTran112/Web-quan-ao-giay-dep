<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng ký tài khoản</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container" style="max-width: 500px; margin-top: 100px;">
    <div class="card shadow-sm">
        <div class="card-body">
            <h4 class="text-center mb-4">Đăng ký tài khoản</h4>

            <c:if test="${not empty error}">
                <div class="alert alert-danger"><c:out value="${error}"/></div>
            </c:if>

            <form action="${pageContext.request.contextPath}/register" method="post">
                <div class="mb-3">
                    <label class="form-label">Tài khoản</label>
                    <input type="text" name="username" class="form-control" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Họ và tên</label>
                    <input type="text" name="fullName" class="form-control" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Số điện thoại</label>
                    <input type="text" name="phone" class="form-control" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Địa chỉ</label>
                    <input type="text" name="address" class="form-control" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Mật khẩu</label>
                    <input type="password" name="password" class="form-control" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Xác nhận mật khẩu</label>
                    <input type="password" name="confirmPassword" class="form-control" required>
                </div>
                <button type="submit" class="btn btn-primary w-100">Đăng ký</button>
            </form>
            <p class="text-center mt-3">
                Đã có tài khoản? <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
            </p>
        </div>
    </div>
</div>
</body>
</html>
