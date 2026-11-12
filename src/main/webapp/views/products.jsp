<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Danh mục sản phẩm | Shop 24162039</title><link rel="stylesheet" href="${ctx}/css/style.css?v=20261112-1"></head><body>
<header class="site-header">
  <a class="brand" href="${ctx}/">Shop<span>24162039</span></a>
  <nav class="site-nav">
    <a href="${ctx}/">Trang chủ</a>
    <a class="active" href="${ctx}/products">Sản phẩm</a>
    <c:choose>
      <c:when test="${not empty sessionScope.account}">
        <c:if test="${sessionScope.account.roleid == 1 or sessionScope.account.roleid == 2}"><a href="${ctx}/dashboard">Dashboard</a></c:if>
        <span class="who">Xin chào, <c:out value="${sessionScope.account.fullName}"/></span>
        <form action="${ctx}/logout" method="post" class="inline"><input type="hidden" name="csrf" value="${sessionScope.csrf}"><button class="link-button">Đăng xuất</button></form>
      </c:when>
      <c:otherwise>
        <a href="${ctx}/login">Đăng nhập</a>
        <a class="button small" href="${ctx}/register">Đăng ký</a>
      </c:otherwise>
    </c:choose>
  </nav>
</header>
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
        <article class="product-card">
          <div class="product-media"><img src="${ctx}/${p.image}" alt="<c:out value='${p.name}'/>" loading="lazy"></div>
          <div class="product-body">
            <span class="tag"><c:out value="${p.category.name}"/></span>
            <h3><c:out value="${p.name}"/></h3>
            <p class="desc"><c:out value="${p.description}"/></p>
            <p class="price"><fmt:formatNumber value="${p.price}" type="number" maxFractionDigits="0"/> ₫</p>
          </div>
        </article>
      </c:forEach>
      <c:if test="${empty productList}"><p class="muted empty-state">Không tìm thấy sản phẩm phù hợp. Hãy thử danh mục khác hoặc từ khóa khác.</p></c:if>
    </div>
  </section>
</main>
<footer class="site-footer">Nguyễn Đức Học · MSSV 24162039 · Ứng dụng JPA/Hibernate</footer>
</body></html>
