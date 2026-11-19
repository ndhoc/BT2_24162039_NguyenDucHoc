<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="showProductDescription" value="${true}"/>
<fmt:setLocale value="vi_VN"/>
<!doctype html>
<html><head><title>Danh mục sản phẩm | Shop 24162039</title></head><body>
<main class="page">
  <section class="section">
    <div class="section-head">
      <div><p class="eyebrow">Cửa hàng</p><h1>Danh mục sản phẩm</h1></div>
      <form class="search" method="get" action="${ctx}/products">
        <c:if test="${not empty selectedCategoryId}"><input type="hidden" name="categoryId" value="${selectedCategoryId}"></c:if>
        <input name="q" value="<c:out value='${keyword}'/>" placeholder="Tìm sản phẩm..." aria-label="Tìm sản phẩm">
        <button>Tìm</button>
      </form>
    </div>

    <div class="chips">
      <a class="chip ${empty selectedCategoryId ? 'active' : ''}" href="${ctx}/products">Tất cả</a>
      <c:forEach items="${cateList}" var="cate">
        <a class="chip ${selectedCategoryId == cate.id ? 'active' : ''}" href="${ctx}/products?categoryId=${cate.id}"><c:out value="${cate.name}"/></a>
      </c:forEach>
    </div>

    <p class="muted result-count">${productList.size()} sản phẩm<c:if test="${not empty keyword}"> cho “<c:out value="${keyword}"/>”</c:if></p>

    <div class="product-grid">
      <c:forEach items="${productList}" var="p">
        <%@ include file="/views/components/product-card.jsp" %>
      </c:forEach>
      <c:if test="${empty productList}"><p class="muted empty-state">Không tìm thấy sản phẩm phù hợp. Hãy thử danh mục khác hoặc từ khóa khác.</p></c:if>
    </div>
  </section>
</main>
</body></html>
