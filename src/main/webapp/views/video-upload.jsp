<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Tải video lên | Shop 24162039</title><link rel="stylesheet" href="${ctx}/css/style.css?v=20261119-1"></head><body>
<header class="site-header">
  <a class="brand" href="${ctx}/home">Shop<span>24162039</span></a>
  <nav class="site-nav">
    <a href="${ctx}/home">Trang chủ</a>
    <a href="${ctx}/products">Sản phẩm</a>
    <a href="${ctx}/video">Video</a>
    <span class="who">Xin chào, <c:out value="${sessionScope.account.fullName}"/></span>
    <form action="${ctx}/logout" method="post" class="inline"><input type="hidden" name="csrf" value="${sessionScope.csrf}"><button class="link-button">Đăng xuất</button></form>
  </nav>
</header>
<main class="card auth">
<p class="eyebrow">Upload Multipart</p><h1>Tải video lên</h1>
<p class="muted">Chọn tệp MP4, WebM hoặc OGG tối đa 50 MB. Video được lưu ngoài WAR tại <code>uploads/video</code> và gắn với tài khoản của bạn.</p>
<c:if test="${not empty alert}"><p class="alert" role="alert"><c:out value="${alert}"/></p></c:if>
<form action="${ctx}/video/upload" method="post" enctype="multipart/form-data">
  <input type="hidden" name="csrf" value="${sessionScope.csrf}">
  <label for="title">Tiêu đề video</label>
  <input id="title" name="title" maxlength="255" value="<c:out value='${titleValue}'/>" required>
  <label for="description">Mô tả</label>
  <textarea id="description" name="description" maxlength="1000" rows="3"><c:out value="${descriptionValue}"/></textarea>
  <label for="videoFile">Tệp video</label>
  <input id="videoFile" type="file" name="videoFile" accept="video/mp4,video/webm,video/ogg" required>
  <button type="submit">Tải lên</button>
</form>
<p class="auth-footer"><a href="${ctx}/video">← Danh sách video của tôi</a></p>
</main></body></html>
