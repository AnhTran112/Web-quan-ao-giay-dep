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

<!-- Thêm thư viện Chart.js -->
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<div class="row mt-4">
    <!-- Cột chứa biểu đồ -->
    <div class="col-md-8">
        <div class="card">
            <div class="card-header bg-white fw-bold">Biểu đồ doanh thu 6 tháng gần nhất</div>
            <div class="card-body">
                <canvas id="revenueChart" height="100"></canvas>
            </div>
        </div>
    </div>
    
    <!-- Cột chứa danh sách -->
    <div class="col-md-4">
        <div class="card mb-3">
            <div class="card-header text-bg-warning fw-bold"> Top Sản Phẩm Bán Chạy</div>
            <ul class="list-group list-group-flush">
                <c:forEach var="prod" items="${topProducts}">
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        ${prod.name}
                        <span class="badge bg-primary rounded-pill">${prod.total_sold} đã bán</span>
                    </li>
                </c:forEach>
            </ul>
        </div>
        
        <div class="card">
            <div class="card-header text-bg-danger text-white fw-bold"> Cảnh Báo Sắp Hết Hàng</div>
            <ul class="list-group list-group-flush">
                <c:forEach var="out" items="${outOfStock}">
                    <li class="list-group-item d-flex justify-content-between align-items-center text-danger">
                        ${out.name}
                        <span class="badge bg-danger rounded-pill">Còn ${out.quantity}</span>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </div>
</div>

<script>
    document.addEventListener("DOMContentLoaded", function() {
        const ctx = document.getElementById('revenueChart').getContext('2d');
        const revenueChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: ${chartLabels}, // Dữ liệu từ Servlet
                datasets: [{
                    label: 'Doanh thu (VNĐ)',
                    data: ${chartData}, // Dữ liệu từ Servlet
                    backgroundColor: 'rgba(54, 162, 235, 0.6)',
                    borderColor: 'rgba(54, 162, 235, 1)',
                    borderWidth: 1,
                    borderRadius: 4
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: { beginAtZero: true }
                }
            }
        });
    });
</script>

<%@ include file="admin-footer.jsp" %>
