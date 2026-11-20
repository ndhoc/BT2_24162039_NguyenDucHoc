<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<fmt:setLocale value="vi_VN"/>
<!doctype html>
<html><head><title>Trang chủ | Shop 24162039</title></head><body>
<main class="page">
  <section class="hero">
    <div class="hero-copy">
      <p class="eyebrow">Shop 24162039</p>
      <h1>Công nghệ cho<br>mỗi ngày của bạn.</h1>
      <p class="muted">Khám phá điện thoại, laptop và những cuốn sách cho công việc, học tập và cuộc sống.</p>
      <div class="hero-actions">
        <a class="button" href="${ctx}/products">Xem sản phẩm</a>
        <a class="button secondary" href="#categories">Khám phá danh mục</a>
      </div>
    </div>
    <%-- Photo source: https://images.unsplash.com/photo-1496181133206-80ce9b88a853 --%>
    <div class="hero-art"><img src="https://res.cloudinary.com/djr3qad5v/image/upload/v1795144660/shop-24162039/site-assets/store-hero-9bfe9a48366f2130.jpg" width="1200" height="800" alt="Laptop trong góc làm việc sáng sủa" fetchpriority="high"></div>
  </section>



  <section class="section">
    <div class="section-head" id="categories"><h2>Khám phá theo danh mục</h2><a href="${ctx}/products">Xem tất cả →</a></div>
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
        <%@ include file="/views/components/product-card.jsp" %>
      </c:forEach>
      <c:if test="${empty productList}"><p class="muted empty-state">Cửa hàng đang cập nhật sản phẩm. Bạn hãy quay lại sau nhé.</p></c:if>
    </div>
  </section>
  <section class="section">
    <div class="section-head"><h2>Video mới nhất</h2><a href="${ctx}/video">Xem tất cả video →</a></div>
    <div class="video-grid">
      <c:forEach items="${videoList}" var="v">
        <article class="video-card">
          <a class="video-media" href="${ctx}/video/detail?id=${v.id}">
            <video preload="metadata" muted playsinline>
              <c:url value="/video-file" var="fileUrl"><c:param name="fname" value="${v.fileName}"/></c:url>
              <source src="${fileUrl}">
            </video>
            <span class="play-badge" aria-hidden="true">▶</span>
          </a>
          <div class="video-body">
            <h3><a href="${ctx}/video/detail?id=${v.id}"><c:out value="${v.title}"/></a></h3>
            <p class="desc"><c:out value="${v.description}"/></p>
            <p class="muted"><c:out value="${v.user.username}"/> · <fmt:formatDate value="${v.createdAt}" pattern="dd/MM/yyyy HH:mm"/></p>
          </div>
        </article>
      </c:forEach>
      <c:if test="${empty videoList}"><p class="muted empty-state">Chưa có video nào. Hãy tải video đầu tiên lên.</p></c:if>
    </div>
  </section>
</main>
</body></html>
