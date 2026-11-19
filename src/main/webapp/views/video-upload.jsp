<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!doctype html>
<html><head><title>Tải video lên | Shop 24162039</title></head><body>
<main class="card auth">
<p class="eyebrow">Upload Multipart</p><h1>Tải video lên</h1>
<p class="muted">Chọn tệp MP4, WebM hoặc OGG tối đa 50 MB. Video được lưu ngoài WAR tại <code>uploads/video</code> và gắn với tài khoản của bạn.</p>
<c:if test="${not empty alert}"><p class="alert" role="alert"><c:out value="${alert}"/></p></c:if>
<form action="${ctx}/video/upload" method="post" enctype="multipart/form-data">
  <input type="hidden" name="csrf" value="${sessionScope.csrf}">
  <label for="title">Tiêu đề video</label>
  <input id="title" name="title" aria-invalid="${not empty fieldErrors.title}" aria-describedby="title-error" class="form-control${empty fieldErrors.title ? '' : ' is-invalid'}" data-nonblank maxlength="255" value="<c:out value='${titleValue}'/>" required>
  <form:field-error field="title" />
  <label for="description">Mô tả</label>
  <textarea id="description" name="description" aria-invalid="${not empty fieldErrors.description}" aria-describedby="description-error" class="form-control${empty fieldErrors.description ? '' : ' is-invalid'}" maxlength="1000" rows="3"><c:out value="${descriptionValue}"/></textarea>
  <form:field-error field="description" />
  <label for="videoFile">Tệp video</label>
  <input id="videoFile" type="file" name="videoFile" aria-invalid="${not empty fieldErrors.videoFile}" aria-describedby="videoFile-error" class="form-control${empty fieldErrors.videoFile ? '' : ' is-invalid'}" data-max-mb="50" accept="video/mp4,video/webm,video/ogg" required>
  <form:field-error field="videoFile" />
  <button class="btn btn-primary" type="submit">Tải lên</button>
</form>
<p class="auth-footer"><a href="${ctx}/video">← Danh sách video của tôi</a></p>
</main></body></html>
