<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:url value="/video-file" var="fileUrl"><c:param name="fname" value="${video.fileName}"/></c:url>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title><c:out value="${video.title}"/> | Shop 24162039</title><link rel="stylesheet" href="${ctx}/css/style.css?v=20261119-1"></head><body>
<header class="site-header">
  <a class="brand" href="${ctx}/home">Shop<span>24162039</span></a>
  <nav class="site-nav">
    <a href="${ctx}/home">Trang chủ</a>
    <a href="${ctx}/products">Sản phẩm</a>
    <a class="active" href="${ctx}/video">Video</a>
    <c:choose>
      <c:when test="${not empty sessionScope.account}">
        <c:if test="${sessionScope.account.roleid == 1 or sessionScope.account.roleid == 2}"><a href="${ctx}/dashboard">Dashboard</a></c:if>
        <span class="who">Xin chào, <c:out value="${sessionScope.account.fullName}"/></span>
        <form action="${ctx}/logout" method="post" class="inline"><input type="hidden" name="csrf" value="${sessionScope.csrf}"><button class="link-button">Đăng xuất</button></form>
      </c:when>
      <c:otherwise><a href="${ctx}/login">Đăng nhập</a><a href="${ctx}/register">Đăng ký</a></c:otherwise>
    </c:choose>
  </nav>
</header>
<main class="page">
  <article class="video-detail">
    <p class="eyebrow">Chi tiết video</p>
    <h1><c:out value="${video.title}"/></h1>
    <p class="muted">
      Đăng bởi <strong><c:out value="${video.user.username}"/></strong>
      · <fmt:formatDate value="${video.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
    </p>
    <div class="video-player">
      <video controls autoplay preload="metadata" playsinline>
        <source src="${fileUrl}">
        Trình duyệt của bạn không hỗ trợ thẻ video.
      </video>
    </div>
    <c:if test="${not empty video.description}"><p class="video-description"><c:out value="${video.description}"/></p></c:if>
    <p><a href="${fileUrl}" download>Tải video xuống</a> · <a href="${ctx}/home">← Trang chủ</a> · <a href="${ctx}/video">Danh sách video</a></p>
  </article>
</main>
<footer class="site-footer">Nguyễn Đức Học · MSSV 24162039</footer>
</body></html>
