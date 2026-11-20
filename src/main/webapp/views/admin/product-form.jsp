<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!doctype html>
<html>
<head><title>${editing ? 'Sửa sản phẩm' : 'Thêm sản phẩm'} | 24162039</title></head>
<body>
<main class="card auth">
  <h1>${editing ? 'Sửa sản phẩm' : 'Thêm sản phẩm'}</h1>
  <c:if test="${not empty alert}"><p class="alert" role="alert"><c:out value="${alert}"/></p></c:if>
  <c:if test="${empty categoryList}">
    <p><a href="${ctx}/admin/category/add">Thêm danh mục</a> để phân loại sản phẩm.</p>
  </c:if>
  <form action="${ctx}/admin/product/${editing ? 'edit' : 'add'}" method="post" enctype="multipart/form-data">
    <input type="hidden" name="csrf" value="${sessionScope.csrf}">
    <c:if test="${editing}"><input type="hidden" name="id" value="${product.id}"></c:if>

    <label for="name">Tên sản phẩm</label>
    <input class="form-control" id="name" name="name" maxlength="255" data-nonblank required
           aria-invalid="${not empty fieldErrors.name}" aria-describedby="name-error"
           value="<c:out value='${submitted ? param.name : product.name}'/>">
    <form:field-error field="name"/>

    <label for="price">Giá (₫)</label>
    <input class="form-control" id="price" name="price" inputmode="decimal" maxlength="19"
           pattern="[0-9]{1,16}([.,][0-9]{1,2})?" required
           aria-invalid="${not empty fieldErrors.price}" aria-describedby="price-hint price-error"
           value="<c:out value='${submitted ? param.price : product.price}'/>">
    <small class="muted" id="price-hint">Nhập số tiền, ví dụ 320000. Có thể nhập tối đa 2 chữ số thập phân.</small>
    <form:field-error field="price"/>

    <label for="categoryId">Danh mục</label>
    <select class="form-select" id="categoryId" name="categoryId" required
            aria-invalid="${not empty fieldErrors.categoryId}" aria-describedby="categoryId-error">
      <option value="">Chọn danh mục</option>
      <c:forEach items="${categoryList}" var="cate">
        <c:set var="categoryValue">${cate.id}</c:set>
        <option value="${cate.id}" ${(submitted ? param.categoryId eq categoryValue : product.category.id eq cate.id) ? 'selected' : ''}>
          <c:out value="${cate.name}"/>
        </option>
      </c:forEach>
    </select>
    <form:field-error field="categoryId"/>

    <label for="description">Mô tả</label>
    <textarea class="form-control" id="description" name="description" rows="4" maxlength="1000"
              aria-invalid="${not empty fieldErrors.description}" aria-describedby="description-error"><c:out value="${submitted ? param.description : product.description}"/></textarea>
    <form:field-error field="description"/>

    <c:if test="${not empty product.image}">
      <img class="preview" src="<c:out value='${product.image.startsWith("https://") ? product.image : ctx.concat("/").concat(product.image)}'/>" alt="Ảnh hiện tại của sản phẩm">
    </c:if>
    <label for="image">Ảnh sản phẩm</label>
    <input class="form-control" id="image" name="image" type="file" accept="image/png,image/jpeg" data-max-mb="5"
           aria-invalid="${not empty fieldErrors.image}" aria-describedby="image-hint image-error">
    <small class="muted" id="image-hint">PNG hoặc JPEG, tối đa 5 MB. <c:if test="${not empty product.image}">Để trống để giữ ảnh hiện tại.</c:if></small>
    <form:field-error field="image"/>

    <button class="btn btn-primary" type="submit">${editing ? 'Lưu thay đổi' : 'Thêm sản phẩm'}</button>
    <a href="${ctx}/admin/product/list">Hủy</a>
  </form>
</main>
</body>
</html>
