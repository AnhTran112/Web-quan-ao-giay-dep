package com.shop.controller.admin;

import com.shop.dao.CategoryDAO;
import com.shop.dao.ProductDAO;
import com.shop.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Quan ly san pham (admin). URL: "/admin/products"
 * action = list | new | edit | delete
 *
 * NGUOI PHU TRACH: Nguoi 1 (Hoang).
 */
@WebServlet("/admin/products")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1 MB: duoi nguong nay giu trong RAM, tren moi ghi ra disk tam
    maxFileSize       = 5 * 1024 * 1024,  // 5 MB: gioi han moi file anh
    maxRequestSize    = 10 * 1024 * 1024  // 10 MB: gioi han toan bo request (anh + cac truong khac)
)
public class AdminProductServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "new":
                // Them moi: chi can danh sach danh muc cho o select, khong co san pham cu
                req.setAttribute("categories", categoryDAO.getAll());
                req.getRequestDispatcher("/WEB-INF/views/admin/product-form.jsp").forward(req, resp);
                break;
            case "edit":
                // Sua: doc id tren URL, lay san pham cu de form hien du lieu
                int editId = Integer.parseInt(req.getParameter("id"));
                req.setAttribute("product", productDAO.getById(editId));
                req.setAttribute("categories", categoryDAO.getAll());
                req.getRequestDispatcher("/WEB-INF/views/admin/product-form.jsp").forward(req, resp);
                break;
            case "delete":
                int deleteId = Integer.parseInt(req.getParameter("id"));
                // Lay san pham truoc khi xoa de biet ten file anh -> xoa luon file anh khoi o dia
                Product deleting = productDAO.getById(deleteId);
                productDAO.delete(deleteId);
                if (deleting != null) deleteImageFile(deleting.getImage());
                resp.sendRedirect(req.getContextPath() + "/admin/products");
                break;
            default:
                // Tim kiem theo ten neu co tham so keyword
                String keyword = req.getParameter("keyword");
                List<Product> products;
                if (keyword != null && !keyword.trim().isEmpty()) {
                    products = productDAO.search(keyword.trim());
                    req.setAttribute("keyword", keyword); // giu lai gia tri trong o search
                } else {
                    products = productDAO.getAll();
                }
                req.setAttribute("products", products);
                req.getRequestDispatcher("/WEB-INF/views/admin/product-list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Doc du lieu tu form (de dang String de tu kiem tra truoc khi doi kieu)
        String idParam = req.getParameter("id");
        String name = req.getParameter("name");
        String categoryIdParam = req.getParameter("categoryId");
        String priceParam = req.getParameter("price");
        String quantityParam = req.getParameter("quantity");
        String description = req.getParameter("description");

        // Giu ten anh cu (hidden field). Se bi ghi de neu admin chon file moi.
        String oldImage = req.getParameter("image");
        String image = oldImage;

        // ===== XU LY UPLOAD ANH =====
        // Part "imageFile" chi ton tai neu form dung enctype="multipart/form-data"
        // va @MultipartConfig da duoc khai bao tren class nay.
        try {
            Part imagePart = req.getPart("imageFile");
            if (imagePart != null && imagePart.getSize() > 0) {
                // Lay phan mo rong (.jpg, .png...) tu ten file goc de giu dung dinh dang.
                // Neu ten file khong co dau cham thi mac dinh .jpg de tranh loi cat chuoi.
                String originalName = imagePart.getSubmittedFileName();
                String ext = ".jpg";
                if (originalName != null && originalName.contains(".")) {
                    ext = originalName.substring(originalName.lastIndexOf('.'));
                }

                // Dung timestamp lam ten file de tranh trung ten khi nhieu san pham co anh giong nhau
                String fileName = System.currentTimeMillis() + ext;

                // getRealPath tra ve duong dan that tren dia cua thu muc da deploy
                String uploadDir = getServletContext().getRealPath("/assets/images/");
                new File(uploadDir).mkdirs(); // tao thu muc neu chua co
                imagePart.write(uploadDir + File.separator + fileName);
                image = fileName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ===== DOC % GIAM GIA (kep trong khoang 0-100) =====
        int discountPercent = 0;
        try {
            discountPercent = Integer.parseInt(req.getParameter("discountPercent"));
        } catch (Exception ignore) {}
        if (discountPercent < 0) discountPercent = 0;
        if (discountPercent > 100) discountPercent = 100;

        // ===== DOC PHAN LOAI (variants): 3 mang song song, bo dong ten rong =====
        java.util.List<com.shop.model.ProductVariant> variants = new java.util.ArrayList<>();
        String[] vNames = req.getParameterValues("variantName");
        String[] vPrices = req.getParameterValues("variantPrice");
        String[] vQtys = req.getParameterValues("variantQty");
        if (vNames != null) {
            for (int i = 0; i < vNames.length; i++) {
                String vn = vNames[i] == null ? "" : vNames[i].trim();
                if (vn.isEmpty()) continue; // bo dong khong nhap ten
                java.math.BigDecimal vp = java.math.BigDecimal.ZERO;
                int vq = 0;
                try { if (vPrices != null) vp = new java.math.BigDecimal(vPrices[i]); } catch (Exception ignore) {}
                try { if (vQtys != null) vq = Integer.parseInt(vQtys[i]); } catch (Exception ignore) {}
                variants.add(new com.shop.model.ProductVariant(0, vn, vp, vq));
            }
        }

        // ===== VALIDATE PHIA SERVER =====
        String error = null;
        BigDecimal price = null;
        int quantity = 0;
        int categoryId = 0;

        if (name == null || name.trim().isEmpty()) {
            error = "Tên sản phẩm không được để trống.";
        } else {
            try {
                price      = new BigDecimal(priceParam);
                quantity   = Integer.parseInt(quantityParam);
                categoryId = Integer.parseInt(categoryIdParam);
                if (price.signum() < 0) error = "Giá phải lớn hơn hoặc bằng 0.";
                else if (quantity < 0)  error = "Số lượng phải lớn hơn hoặc bằng 0.";
            } catch (NumberFormatException e) {
                error = "Giá và số lượng phải là số hợp lệ.";
            }
        }

        // Dong goi vao object Product
        Product p = new Product();
        p.setName(name);
        p.setCategoryId(categoryId);
        p.setPrice(price);
        p.setQuantity(quantity);
        p.setImage(image);
        p.setDescription(description);
        p.setDiscountPercent(discountPercent);
        p.setVariants(variants);
        if (idParam != null && !idParam.isEmpty()) p.setId(Integer.parseInt(idParam));

        // Neu co loi -> quay lai form, giu lai du lieu da nhap + bao loi
        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("product", p);
            req.setAttribute("categories", categoryDAO.getAll());
            req.getRequestDispatcher("/WEB-INF/views/admin/product-form.jsp").forward(req, resp);
            return;
        }

        // id rong -> them moi; co id -> cap nhat
        if (idParam == null || idParam.isEmpty()) {
            productDAO.insert(p);
        } else {
            productDAO.update(p);
            // Neu admin chon anh moi (ten file thay doi) thi xoa anh cu cho do rac
            if (oldImage != null && !oldImage.isEmpty() && !oldImage.equals(image)) {
                deleteImageFile(oldImage);
            }
        }

        resp.sendRedirect(req.getContextPath() + "/admin/products");
    }

    /**
     * Xoa 1 file anh trong thu muc assets/images cua ung dung da deploy.
     * Goi khi admin doi sang anh moi (xoa anh cu) hoac xoa han san pham,
     * de tranh tich tu cac file anh khong con duoc dung den.
     */
    private void deleteImageFile(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) return;
        String uploadDir = getServletContext().getRealPath("/assets/images/");
        File f = new File(uploadDir, fileName);
        if (f.exists()) f.delete();
    }
}
