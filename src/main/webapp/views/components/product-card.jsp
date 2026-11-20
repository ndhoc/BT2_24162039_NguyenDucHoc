<%@ page pageEncoding="UTF-8" %>
<a class="product-card product-link" href="${ctx}/products/detail?id=${p.id}">
  <div class="product-media">
    <c:choose>
      <c:when test="${not empty p.image}">
        <img src="<c:out value='${p.image.startsWith("https://") ? p.image : ctx.concat("/").concat(p.image)}'/>" alt="" loading="lazy">
      </c:when>
      <c:otherwise><span class="product-placeholder">Chưa có ảnh</span></c:otherwise>
    </c:choose>
  </div>
  <div class="product-body">
    <span class="tag"><c:out value="${p.category.name}"/></span>
    <h3><c:out value="${p.name}"/></h3>
    <c:if test="${showProductDescription and not empty p.description}">
      <p class="desc"><c:out value="${p.description}"/></p>
    </c:if>
    <p class="price"><fmt:formatNumber value="${p.price}" type="number" maxFractionDigits="2"/> ₫</p>
  </div>
</a>
