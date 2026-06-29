<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="common/header.jsp" %>

<div class="row">
    <div class="col-12">
        <h2 class="mb-4">Giỏ hàng của bạn</h2>
        
        <c:choose>
            <c:when test="${empty requestScope.cartList}">
                <div class="alert alert-info">Giỏ hàng đang trống. <a href="${pageContext.request.contextPath}/home">Tiếp tục mua sắm</a></div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-bordered table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th scope="col" style="width: 15%">Hình ảnh</th>
                                <th scope="col" style="width: 35%">Sản phẩm</th>
                                <th scope="col" style="width: 15%">Đơn giá</th>
                                <th scope="col" style="width: 15%">Số lượng</th>
                                <th scope="col" style="width: 15%">Thành tiền</th>
                                <th scope="col" style="width: 5%">Xóa</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:set var="totalPrice" value="0" />
                            <c:forEach var="item" items="${requestScope.cartList}">
                                <tr>
                                    <td>
                                        <img src="${pageContext.request.contextPath}/assets/images/${item.image}"
                                             class="img-fluid rounded" alt="${item.name}"
                                             onerror="this.src='https://via.placeholder.com/100x100?text=No+Image'">
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/product?id=${item.productId}" class="text-decoration-none fw-bold">
                                            ${item.name}
                                        </a>
                                    </td>
                                    <td class="text-danger fw-bold">
                                        <fmt:formatNumber value="${item.price}" type="number"/> đ
                                    </td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/cart" method="post" class="d-flex align-items-center">
                                            <input type="hidden" name="action" value="update">
                                            <input type="hidden" name="productId" value="${item.productId}">
                                            <input type="number" name="quantity" value="${item.quantity}" min="1" class="form-control form-control-sm me-2" style="width: 70px;">
                                            <button type="submit" class="btn btn-sm btn-outline-primary">Cập nhật</button>
                                        </form>
                                    </td>
                                    <td class="text-danger fw-bold">
                                        <fmt:formatNumber value="${item.subtotal}" type="number"/> đ
                                    </td>
                                    <td class="text-center">
                                        <form action="${pageContext.request.contextPath}/cart" method="post" onsubmit="return confirm('Bạn có chắc muốn xóa sản phẩm này?');">
                                            <input type="hidden" name="action" value="remove">
                                            <input type="hidden" name="productId" value="${item.productId}">
                                            <button type="submit" class="btn btn-sm btn-danger">Xóa</button>
                                        </form>
                                    </td>
                                </tr>
                                <c:set var="totalPrice" value="${totalPrice + item.subtotal}" />
                            </c:forEach>
                        </tbody>
                        <tfoot>
                            <tr>
                                <td colspan="4" class="text-end fw-bold fs-5">Tổng cộng:</td>
                                <td colspan="2" class="text-danger fw-bold fs-5">
                                    <fmt:formatNumber value="${totalPrice}" type="number"/> đ
                                </td>
                            </tr>
                        </tfoot>
                    </table>
                </div>
                
                <div class="d-flex justify-content-between mt-4">
                    <a href="${pageContext.request.contextPath}/home" class="btn btn-secondary">Tiếp tục mua sắm</a>
                    <a href="${pageContext.request.contextPath}/checkout" class="btn btn-success btn-lg">Tiến hành thanh toán</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="common/footer.jsp" %>
