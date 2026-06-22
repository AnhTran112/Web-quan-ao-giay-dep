<%@ include file="common/header.jsp" %>

<h3 class="mb-3">Thông tin đặt hàng</h3>

<div class="row">
    <div class="col-md-6">
        <%-- Form nhap thong tin khach. doPost cua CheckoutServlet xu ly --%>
        <form action="${pageContext.request.contextPath}/checkout" method="post">
            <div class="mb-3">
                <label class="form-label">Họ tên</label>
                <input type="text" name="customerName" class="form-control" required>
            </div>
            <div class="mb-3">
                <label class="form-label">Số điện thoại</label>
                <input type="text" name="phone" class="form-control" required>
            </div>
            <div class="mb-3">
                <label class="form-label">Địa chỉ giao hàng</label>
                <textarea name="address" class="form-control" rows="3" required></textarea>
            </div>
            <button type="submit" class="btn btn-success">Xác nhận đặt hàng</button>
        </form>
    </div>
</div>

<%@ include file="common/footer.jsp" %>
