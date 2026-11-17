<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Video | 24162039</title><link rel="stylesheet" href="${ctx}/css/style.css?v=20261119-1"></head><body><main class="card auth">
<p class="eyebrow">Quản lý video</p><h1>${editing ? 'Sửa video' : 'Thêm video'}</h1>
<c:if test="${not empty alert}"><p class="alert" role="alert"><c:out value="${alert}"/></p></c:if>
<form action="${ctx}/admin/video/${editing ? 'edit' : 'add'}" method="post" enctype="multipart/form-data">
  <input type="hidden" name="csrf" value="${sessionScope.csrf}">
  <input type="hidden" name="id" value="${video.id}">
  <label for="title">Tiêu đề</label>
  <input id="title" name="title" maxlength="255" value="<c:out value='${video.title}'/>" required>
  <label for="description">Mô tả</label>
  <textarea id="description" name="description" maxlength="1000" rows="3"><c:out value="${video.description}"/></textarea>
  <label for="userId">Chủ sở hữu</label>
  <select id="userId" name="userId" required>
    <c:forEach items="${userList}" var="u">
      <option value="${u.id}" ${video.user.id == u.id ? 'selected' : ''}><c:out value="${u.username}"/> — <c:out value="${u.fullName}"/></option>
    </c:forEach>
  </select>
  <c:if test="${not empty video.fileName}">
    <c:url value="/video-file" var="fileUrl"><c:param name="fname" value="${video.fileName}"/></c:url>
    <video class="preview-video" controls preload="metadata"><source src="${fileUrl}"></video>
  </c:if>
  <label for="videoFile">Tệp video</label>
  <input id="videoFile" type="file" name="videoFile" accept="video/mp4,video/webm,video/ogg" ${editing ? '' : 'required'}>
  <p class="muted">MP4/WebM/OGG, tối đa 50 MB. ${editing ? 'Để trống để giữ video hiện tại.' : ''}</p>
  <button type="submit">${editing ? 'Lưu thay đổi' : 'Thêm video'}</button>
  <a href="${ctx}/admin/video/list">Hủy</a>
</form></main></body></html>
