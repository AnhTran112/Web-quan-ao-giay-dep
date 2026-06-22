<%@ include file="common/header.jsp" %>

<%-- Chi tiet san pham. Du lieu: request attribute "product" --%>
<div class="row">
    <div class="col-md-5">
        <img src="${pageContext.request.contextPath}/assets/images/${product.image}"
             class="img-fluid rounded" alt="${product.name}"
             onerror="this.src='https://via.placeholder.com/400x300?text=No+Image'">
    </div>
    <div class="col-md-7">
        <h3>${product.name}</h3>
        <p class="text-danger fs-4 fw-bold">
            <fmt:formatNumber value="${product.price}" type="number"/> đ
        </p>
        <p>${product.description}</p>

        <%-- Form them vao gio hang --%>
        <form action="${pageContext.request.contextPath}/cart" method="post">
            <input type="hidden" name="action" value="add">
            <input type="hidden" name="productId" value="${product.id}">
            <div class="input-group mb-3" style="max-width: 200px;">
                <input type="number" name="quantity" value="1" min="1" class="form-control">
            </div>
            <button type="submit" class="btn btn-success">🛒 Thêm vào giỏ</button>
        </form>
    </div>
</div>

<%@ include file="common/footer.jsp" %>
