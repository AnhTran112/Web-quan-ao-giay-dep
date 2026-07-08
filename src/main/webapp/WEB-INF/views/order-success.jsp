<%@ include file="common/header.jsp" %>

<div class="text-center py-5">
    <h1 class="text-success">✓ Đặt hàng thành công!</h1>
    <c:if test="${not empty orderId}">
        <h4 class="mt-3">Mã đơn hàng của bạn là: <span class="badge bg-primary">#${orderId}</span></h4>
    </c:if>
    <p class="lead mt-3">Cảm ơn bạn đã mua hàng. Chúng tôi sẽ liên hệ sớm nhất.</p>
    <a href="${pageContext.request.contextPath}/home" class="btn btn-primary mt-3">Tiếp tục mua sắm</a>
</div>

<%@ include file="common/footer.jsp" %>
