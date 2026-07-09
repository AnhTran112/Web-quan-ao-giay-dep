<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container" style="max-width: 400px; margin-top: 100px;">
    <div class="card shadow-sm">
        <div class="card-body">
            <h4 class="text-center mb-4">Đăng nhập</h4>

            <c:if test="${not empty error}">
                <div class="alert alert-danger"><c:out value="${error}"/></div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="post">
                <div class="mb-3">
                    <label class="form-label">Tài khoản</label>
                    <input type="text" name="username" class="form-control" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Mật khẩu</label>
                    <input type="password" name="password" class="form-control" required>
                </div>
                <div class="mb-3 form-check">
                    <input type="checkbox" class="form-check-input" name="remember" id="rememberMe">
                    <label class="form-check-label" for="rememberMe">Ghi nhớ đăng nhập (30 ngày)</label>
                </div>
                <button type="submit" class="btn btn-primary w-100">Đăng nhập</button>
            </form>
            <p class="text-center mt-3">
                Chưa có tài khoản? <a href="${pageContext.request.contextPath}/register">Đăng ký ngay</a>
            </p>
        </div>
    </div>
</div>
</body>
</html>
