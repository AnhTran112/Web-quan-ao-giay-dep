</div><!-- /.container -->
<footer class="bg-dark text-light text-center py-3 mt-5">
    <div class="container">
        <small>Đồ án môn Lập trình Web &copy; 2026 — Nhóm sinh viên</small>
    </div>
    </div>
</footer>

<!-- Quick View Modal -->
<div class="modal fade" id="quickViewModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header border-0">
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body pb-5">
        <div class="row g-4">
          <div class="col-md-5">
            <img id="qvImage" src="" class="img-fluid rounded" alt="Product Image">
          </div>
          <div class="col-md-7">
            <h4 id="qvName" class="fw-bold mb-2">Tên sản phẩm</h4>
            <div class="mb-2 text-muted small" id="qvCategory">Danh mục</div>
            <div class="mb-3">
              <span id="qvPrice" class="fs-4 text-danger fw-bold">0 đ</span>
              <span id="qvOldPrice" class="text-decoration-line-through text-muted ms-2" style="display:none;"></span>
              <span id="qvBadge" class="badge bg-danger ms-2" style="display:none;"></span>
            </div>
            <div id="qvVariants" class="mb-3" style="display:none;">
              <label class="form-label fw-semibold small">Phân loại</label>
              <div class="d-flex flex-wrap gap-2" id="qvVariantList"></div>
            </div>
            
            <form id="qvForm" onsubmit="event.preventDefault(); submitQvAddToCart();">
                <input type="hidden" id="qvProductId" value="">
                <input type="hidden" id="qvVariantId" value="">
                <input type="hidden" id="qvStock" value="">
                <input type="hidden" id="qvHasVariants" value="">
                
                <div class="d-flex align-items-center mb-3">
                    <label class="me-3 fw-semibold small">Số lượng</label>
                    <div class="input-group" style="width: 120px;">
                        <button type="button" class="btn btn-outline-secondary btn-sm" onclick="changeQvQty(-1)">-</button>
                        <input type="number" id="qvQty" class="form-control form-control-sm text-center" value="1" min="1">
                        <button type="button" class="btn btn-outline-secondary btn-sm" onclick="changeQvQty(1)">+</button>
                    </div>
                    <span id="qvStockLabel" class="ms-3 text-muted small"></span>
                </div>
                
                <div class="d-flex gap-2">
                    <button type="submit" id="qvAddBtn" class="btn btn-success">Thêm vào giỏ</button>
                    <a href="#" id="qvDetailLink" class="btn btn-outline-primary">Xem chi tiết</a>
                </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
<script>
    let quickViewModal = null;
    document.addEventListener("DOMContentLoaded", function() {
        quickViewModal = new bootstrap.Modal(document.getElementById('quickViewModal'));
    });

    function openQuickView(id) {
        fetch('${pageContext.request.contextPath}/api/product/quickview?id=' + id)
        .then(res => res.json())
        .then(data => {
            if(!data.success) {
                alert('Không thể tải thông tin sản phẩm');
                return;
            }
            
            document.getElementById('qvProductId').value = data.id;
            document.getElementById('qvVariantId').value = '';
            document.getElementById('qvHasVariants').value = data.hasVariants;
            document.getElementById('qvStock').value = data.quantity;
            
            document.getElementById('qvImage').src = '${pageContext.request.contextPath}/assets/images/' + data.image;
            document.getElementById('qvName').innerText = data.name;
            document.getElementById('qvCategory').innerText = data.category || 'Sản phẩm';
            document.getElementById('qvDetailLink').href = '${pageContext.request.contextPath}/product?id=' + data.id;
            
            let priceEl = document.getElementById('qvPrice');
            let oldPriceEl = document.getElementById('qvOldPrice');
            let badgeEl = document.getElementById('qvBadge');
            
            if (data.discountPercent > 0) {
                if (data.hasVariants) {
                    let minP = data.minPrice * (100 - data.discountPercent) / 100;
                    priceEl.innerText = Number(minP).toLocaleString('vi-VN') + ' đ +';
                    oldPriceEl.innerText = Number(data.minPrice).toLocaleString('vi-VN') + ' đ';
                } else {
                    let p = data.price * (100 - data.discountPercent) / 100;
                    priceEl.innerText = Number(p).toLocaleString('vi-VN') + ' đ';
                    oldPriceEl.innerText = Number(data.price).toLocaleString('vi-VN') + ' đ';
                }
                oldPriceEl.style.display = 'inline';
                badgeEl.innerText = '-' + data.discountPercent + '%';
                badgeEl.style.display = 'inline';
            } else {
                if (data.hasVariants) {
                    priceEl.innerText = Number(data.minPrice).toLocaleString('vi-VN') + ' đ +';
                } else {
                    priceEl.innerText = Number(data.price).toLocaleString('vi-VN') + ' đ';
                }
                oldPriceEl.style.display = 'none';
                badgeEl.style.display = 'none';
            }
            
            let varContainer = document.getElementById('qvVariants');
            let varList = document.getElementById('qvVariantList');
            varList.innerHTML = '';
            
            if (data.hasVariants && data.variants.length > 0) {
                varContainer.style.display = 'block';
                data.variants.forEach(v => {
                    let btn = document.createElement('button');
                    btn.type = 'button';
                    btn.className = 'btn btn-outline-secondary btn-sm qv-var-btn';
                    btn.innerText = v.name;
                    btn.onclick = function() {
                        document.querySelectorAll('.qv-var-btn').forEach(b => {
                            b.classList.remove('active', 'btn-primary');
                            b.classList.add('btn-outline-secondary');
                        });
                        btn.classList.add('active', 'btn-primary');
                        btn.classList.remove('btn-outline-secondary');
                        
                        document.getElementById('qvVariantId').value = v.id;
                        document.getElementById('qvStock').value = v.quantity;
                        document.getElementById('qvStockLabel').innerText = v.quantity + ' sản phẩm có sẵn';
                        let vp = v.price * (100 - data.discountPercent) / 100;
                        priceEl.innerText = Number(vp).toLocaleString('vi-VN') + ' đ';
                    };
                    varList.appendChild(btn);
                });
            } else {
                varContainer.style.display = 'none';
                document.getElementById('qvStockLabel').innerText = data.quantity + ' sản phẩm có sẵn';
            }
            
            document.getElementById('qvQty').value = 1;
            
            if(data.quantity <= 0) {
                document.getElementById('qvAddBtn').disabled = true;
                document.getElementById('qvAddBtn').innerText = 'Hết hàng';
            } else {
                document.getElementById('qvAddBtn').disabled = false;
                document.getElementById('qvAddBtn').innerText = 'Thêm vào giỏ';
            }
            
            quickViewModal.show();
        });
    }
    
    function changeQvQty(delta) {
        let input = document.getElementById('qvQty');
        let stock = parseInt(document.getElementById('qvStock').value) || 1;
        let v = parseInt(input.value) || 1;
        v += delta;
        if(v < 1) v = 1;
        if(v > stock) v = stock;
        input.value = v;
    }
    
    function submitQvAddToCart() {
        let hasV = document.getElementById('qvHasVariants').value === 'true';
        let vId = document.getElementById('qvVariantId').value;
        if (hasV && vId === '') {
            alert('Vui lòng chọn phân loại!');
            return;
        }
        let pId = document.getElementById('qvProductId').value;
        let qty = document.getElementById('qvQty').value;
        
        quickViewModal.hide();
        if(typeof addToCartAjax === 'function') {
            addToCartAjax(pId, qty, vId || 0);
        } else {
            // fallback
            let form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/cart';
            form.innerHTML = '<input type="hidden" name="action" value="add"><input type="hidden" name="productId" value="'+pId+'"><input type="hidden" name="variantId" value="'+(vId||0)+'"><input type="hidden" name="quantity" value="'+qty+'">';
            document.body.appendChild(form);
            form.submit();
        }
    }
</script>
</body>
</html>
