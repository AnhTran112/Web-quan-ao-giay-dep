<%@ include file="admin-header.jsp" %>

<h3 class="mb-4">Thống kê</h3>
<div class="row">
    <div class="col-md-4">
        <div class="card text-bg-primary mb-3">
            <div class="card-body">
                <h6>Tổng doanh thu</h6>
                <h3><fmt:formatNumber value="${totalRevenue}" type="number"/> đ</h3>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card text-bg-success mb-3">
            <div class="card-body">
                <h6>Số đơn hàng</h6>
                <h3>${totalOrders}</h3>
            </div>
        </div>
    </div>
</div>

<%@ include file="admin-footer.jsp" %>
