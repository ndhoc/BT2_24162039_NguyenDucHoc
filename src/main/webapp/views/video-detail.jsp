<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:url value="/video-file" var="fileUrl"><c:param name="fname" value="${video.fileName}"/></c:url>
<!doctype html>
<html><head><title><c:out value="${video.title}"/> | Shop 24162039</title></head><body>
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
</body></html>
