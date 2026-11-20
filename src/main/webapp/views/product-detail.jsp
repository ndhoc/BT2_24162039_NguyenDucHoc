<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<fmt:setLocale value="vi_VN"/>
<!doctype html>
<html>
<head><title><c:out value="${product.name}"/> | Shop 24162039</title></head>
<body>
<main class="page">
  <nav class="product-breadcrumb" aria-label="Đường dẫn">
    <a href="${ctx}/home">Trang chủ</a>
    <span aria-hidden="true">/</span>
    <a href="${ctx}/products">Sản phẩm</a>
  </nav>
  <article class="card wide product-detail">
    <div class="product-detail-media">
      <c:choose>
        <c:when test="${not empty product.image}">
          <img src="<c:out value='${product.image.startsWith("https://") ? product.image : ctx.concat("/").concat(product.image)}'/>" alt="<c:out value='${product.name}'/>">
        </c:when>
        <c:otherwise><span class="product-placeholder">Chưa có ảnh</span></c:otherwise>
      </c:choose>
    </div>
    <div class="product-detail-body">
      <a class="tag" href="${ctx}/products?categoryId=${product.category.id}"><c:out value="${product.category.name}"/></a>
      <h1><c:out value="${product.name}"/></h1>
      <p class="price product-detail-price"><fmt:formatNumber value="${product.price}" type="number" maxFractionDigits="2"/> ₫</p>
      <c:if test="${not empty product.description}">
        <h2>Mô tả sản phẩm</h2>
        <p class="product-description"><c:out value="${product.description}"/></p>
      </c:if>
    </div>
  </article>
</main>
</body>
</html>
