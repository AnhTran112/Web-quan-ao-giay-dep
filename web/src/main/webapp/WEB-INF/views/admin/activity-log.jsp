<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ include file="admin-header.jsp" %>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h3>Nhật ký hoạt động (Audit Log)</h3>
</div>

<div class="card shadow-sm border-0">
    <div class="card-body p-0">
        <table class="table table-hover table-striped mb-0 align-middle">
            <thead class="table-light">
                <tr>
                    <th>ID</th>
                    <th>Thời gian</th>
                    <th>Quản trị viên</th>
                    <th>Thao tác</th>
                    <th>Chi tiết</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="log" items="${logs}">
                    <tr>
                        <td>#${log.id}</td>
                        <td><fmt:formatDate value="${log.createdAt}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
                        <td><strong><c:out value="${log.adminUsername}"/></strong></td>
                        <td><span class="badge bg-info text-dark"><c:out value="${log.action}"/></span></td>
                        <td><c:out value="${log.details}"/></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty logs}">
                    <tr>
                        <td colspan="5" class="text-center text-muted py-4">Chưa có hoạt động nào được ghi nhận.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="admin-footer.jsp" %>
