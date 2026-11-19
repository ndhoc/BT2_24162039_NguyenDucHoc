<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:url value="/video-file" var="fileUrl"><c:param name="fname" value="${video.fileName}"/></c:url>
<!doctype html>
<html><head><title>Xóa video | 24162039</title></head><body><main class="card auth">
<h1>Xóa video</h1>
<p>Bạn có muốn xóa video <strong><c:out value="${video.title}"/></strong> của <c:out value="${video.user.username}"/>?</p>
<video class="preview-video" controls preload="metadata"><source src="${fileUrl}"></video>
<c:if test="${not empty alert}"><p class="alert"><c:out value="${alert}"/></p></c:if>
<form action="${ctx}/admin/video/delete" method="post"><input type="hidden" name="csrf" value="${sessionScope.csrf}"><input type="hidden" name="id" value="${video.id}"><button class="danger" type="submit">Xác nhận xóa</button><a href="${ctx}/admin/video/list">Hủy</a></form>
</main></body></html>
