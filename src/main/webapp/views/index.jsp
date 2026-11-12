<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Trang chủ | Shop 24162039</title><link rel="stylesheet" href="${ctx}/css/style.css?v=20261112-1"></head><body>
<header class="site-header">
  <a class="brand" href="${ctx}/">Shop<span>24162039</span></a>
  <nav class="site-nav">
    <a class="active" href="${ctx}/">Trang chủ</a>
    <a href="${ctx}/products">Sản phẩm</a>
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
  <section class="hero">
    <div class="hero-copy">
      <p class="eyebrow">Lập trình Web · JPA · MVC</p>
      <h1>Mua sắm công nghệ<br>đơn giản và nhanh chóng</h1>
      <p class="muted">Laptop, điện thoại và sách được quản lý bằng JPA/Hibernate trên SQL Server. Đăng nhập để xem danh mục sản phẩm dành riêng cho bạn.</p>
      <div class="hero-actions">
        <a class="button" href="${ctx}/products">Xem sản phẩm</a>
        <c:if test="${empty sessionScope.account}"><a class="button secondary" href="${ctx}/register">Tạo tài khoản</a></c:if>
      </div>
    </div>
    <div class="hero-art" aria-hidden="true"><span>JPA</span><span>Hibernate</span><span>Servlet</span></div>
  </section>

  <section class="section">
    <div class="section-head"><h2>Danh mục sản phẩm</h2><a href="${ctx}/products">Xem tất cả →</a></div>
    <div class="category-grid">
      <c:forEach items="${cateList}" var="cate">
        <a class="category-card" href="${ctx}/products?categoryId=${cate.id}">
          <c:choose>
            <c:when test="${not empty cate.icon}">
              <c:url value="/image" var="catImage"><c:param name="fname" value="${cate.icon}"/></c:url>
              <img src="${catImage}" alt="<c:out value='${cate.name}'/>">
            </c:when>
            <c:otherwise><span class="category-avatar"><c:out value="${cate.name.substring(0,1)}"/></span></c:otherwise>
          </c:choose>
          <strong><c:out value="${cate.name}"/></strong>
        </a>
      </c:forEach>
      <c:if test="${empty cateList}"><p class="muted">Chưa có danh mục.</p></c:if>
    </div>
  </section>

  <section class="section">
    <div class="section-head"><h2>Sản phẩm nổi bật</h2><a href="${ctx}/products">Tất cả sản phẩm →</a></div>
    <div class="product-grid">
      <c:forEach items="${productList}" var="p">
        <article class="product-card">
          <div class="product-media"><img src="${ctx}/${p.image}" alt="<c:out value='${p.name}'/>" loading="lazy"></div>
          <div class="product-body">
            <span class="tag"><c:out value="${p.category.name}"/></span>
            <h3><c:out value="${p.name}"/></h3>
            <p class="price"><fmt:formatNumber value="${p.price}" type="number" maxFractionDigits="0"/> ₫</p>
          </div>
        </article>
      </c:forEach>
      <c:if test="${empty productList}"><p class="muted">Chưa có sản phẩm. Hãy chạy <code>database/seed.sql</code>.</p></c:if>
    </div>
  </section>
</main>
<footer class="site-footer">Nguyễn Đức Học · MSSV 24162039 · Ứng dụng JPA/Hibernate</footer>
</body></html>
