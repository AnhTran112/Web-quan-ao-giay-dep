<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="admin-header.jsp" %>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h3>Thống kê</h3>
    <!-- Nút xuất CSV -->
    <a href="${pageContext.request.contextPath}/admin/dashboard?action=export&fromDate=${fromDate}&toDate=${toDate}" class="btn btn-success">
        Xuất báo cáo (Excel)
    </a>
</div>

<!-- Lọc theo ngày -->
<div class="card mb-4 shadow-sm">
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/admin/dashboard" method="get" class="row gx-3 gy-2 align-items-center">
            <div class="col-sm-4">
                <label class="visually-hidden">Từ ngày</label>
                <div class="input-group">
                    <div class="input-group-text">Từ ngày</div>
                    <input type="date" class="form-control" name="fromDate" value="${fromDate}">
                </div>
            </div>
            <div class="col-sm-4">
                <label class="visually-hidden">Đến ngày</label>
                <div class="input-group">
                    <div class="input-group-text">Đến ngày</div>
                    <input type="date" class="form-control" name="toDate" value="${toDate}">
                </div>
            </div>
            <div class="col-auto">
                <button type="submit" class="btn btn-primary">Lọc dữ liệu</button>
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-secondary">Xóa lọc</a>
            </div>
        </form>
    </div>
</div>

<div class="row g-3 mb-2">
    <div class="col-md-6">
        <div class="stat-card">
            <div class="stat-label">Tổng doanh thu (đã giao)</div>
            <div class="stat-value text-revenue"><fmt:formatNumber value="${totalRevenue}" type="number"/> đ</div>
        </div>
    </div>
    <div class="col-md-6">
        <div class="stat-card">
            <div class="stat-label">Tổng số đơn hàng</div>
            <div class="stat-value">${totalOrders}</div>
        </div>
    </div>
</div>

<!-- Thêm thư viện Chart.js -->
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<div class="row mt-2">
    <!-- Cột chứa biểu đồ Bar -->
    <div class="col-md-8">
        <div class="card shadow-sm mb-4">
            <div class="card-header bg-white fw-bold">Biểu đồ doanh thu 6 tháng gần nhất</div>
            <div class="card-body">
                <canvas id="revenueChart" height="100"></canvas>
            </div>
        </div>
        
        <!-- 5 đơn hàng mới nhất -->
        <div class="card shadow-sm mb-4">
            <div class="card-header bg-white fw-bold">5 Đơn hàng mới nhất</div>
            <div class="table-responsive">
                <table class="table table-hover mb-0">
                    <thead class="table-light">
                        <tr>
                            <th>Mã ĐH</th>
                            <th>Khách hàng</th>
                            <th>Tổng tiền</th>
                            <th>Trạng thái</th>
                            <th>Ngày tạo</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="o" items="${latestOrders}">
                            <tr>
                                <td>#${o.id}</td>
                                <td><c:out value="${o.customer_name}"/></td>
                                <td><fmt:formatNumber value="${o.total_amount}" type="number"/> đ</td>
                                <td>
                                    <span class="badge ${o.status == 'DELIVERED' ? 'bg-success' : (o.status == 'CANCELLED' ? 'bg-danger' : 'bg-warning text-dark')}">
                                        ${o.status}
                                    </span>
                                </td>
                                <td>${o.created_at}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    
    <!-- Cột chứa biểu đồ Pie và danh sách -->
    <div class="col-md-4">
        <!-- Biểu đồ Pie Danh mục -->
        <div class="card shadow-sm mb-4">
            <div class="card-header bg-white fw-bold">Tỷ trọng doanh thu theo danh mục</div>
            <div class="card-body">
                <canvas id="statusChart"></canvas>
            </div>
        </div>

        <div class="card shadow-sm mb-4">
            <div class="card-header text-bg-warning fw-bold">Top Sản Phẩm Bán Chạy</div>
            <ul class="list-group list-group-flush">
                <c:forEach var="prod" items="${topProducts}">
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        <c:out value="${prod.name}"/>
                        <span class="badge bg-primary rounded-pill">${prod.total_sold} đã bán</span>
                    </li>
                </c:forEach>
            </ul>
        </div>
        
        <div class="card shadow-sm mb-4">
            <div class="card-header text-bg-danger text-white fw-bold">Cảnh Báo Sắp Hết Hàng</div>
            <ul class="list-group list-group-flush">
                <c:forEach var="out" items="${outOfStock}">
                    <li class="list-group-item d-flex justify-content-between align-items-center text-danger">
                        <c:out value="${out.name}"/>
                        <span class="badge bg-danger rounded-pill">Còn ${out.quantity}</span>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </div>
</div>

<script>
    document.addEventListener("DOMContentLoaded", function() {
        // Biểu đồ cột Doanh thu
        const ctxBar = document.getElementById('revenueChart').getContext('2d');
        new Chart(ctxBar, {
            type: 'bar',
            data: {
                labels: ${chartLabels},
                datasets: [{
                    label: 'Doanh thu (VNĐ)',
                    data: ${chartData},
                    backgroundColor: 'rgba(54, 162, 235, 0.6)',
                    borderColor: 'rgba(54, 162, 235, 1)',
                    borderWidth: 1,
                    borderRadius: 4
                }]
            },
            options: {
                responsive: true,
                scales: { y: { beginAtZero: true } }
            }
        });

        // Biểu đồ tròn Danh mục
        const ctxPie = document.getElementById('statusChart').getContext('2d');
        const statusLabels = ${statusLabels != null ? statusLabels : '[]'};
        const statusData = ${statusData != null ? statusData : '[]'};
        
        if (statusLabels.length > 0) {
            new Chart(ctxPie, {
                type: 'pie',
                data: {
                    labels: statusLabels,
                    datasets: [{
                        data: statusData,
                        backgroundColor: [
                            '#ff6384', '#36a2eb', '#ffce56', '#4bc0c0', '#9966ff', '#ff9f40', '#e83e8c', '#20c997'
                        ],
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: { position: 'bottom' }
                    }
                }
            });
        }
    });
</script>

<%@ include file="admin-footer.jsp" %>
