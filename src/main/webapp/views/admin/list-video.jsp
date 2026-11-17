<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Quản lý video | 24162039</title><link rel="stylesheet" href="${ctx}/css/style.css?v=20261119-1"></head><body><main class="card wide">
<header><div><p class="eyebrow">Quản trị · Nguyễn Đức Học · 24162039</p><h1>Quản lý video</h1></div><a href="${ctx}/dashboard">Dashboard</a></header>
<c:if test="${not empty param.saved}"><p class="success" role="status">Thao tác video thành công.</p></c:if>
<div class="toolbar">
  <form class="search" method="get" action="${ctx}/admin/video/list"><label for="q">Tìm video</label><input id="q" name="q" value="<c:out value='${param.q}'/>" placeholder="Tiêu đề hoặc tài khoản"><button>Tìm kiếm</button><a href="${ctx}/admin/video/list">Tất cả</a></form>
  <a class="button" href="${ctx}/admin/video/add">Thêm video</a>
</div>
<div class="table-wrap"><table><thead><tr><th>STT</th><th>Mã</th><th>Xem trước</th><th>Tiêu đề</th><th>Chủ sở hữu</th><th>Ngày đăng</th><th>Thao tác</th></tr></thead><tbody>
<c:forEach items="${videoList}" var="v" varStatus="st"><tr>
  <td>${st.count}</td><td>${v.id}</td>
  <td>
    <c:url value="/video-file" var="fileUrl"><c:param name="fname" value="${v.fileName}"/></c:url>
    <video class="thumb-video" preload="metadata" muted><source src="${fileUrl}"></video>
  </td>
  <td><a href="${ctx}/video/detail?id=${v.id}"><c:out value="${v.title}"/></a></td>
  <td><c:out value="${v.user.username}"/></td>
  <td class="nowrap"><fmt:formatDate value="${v.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
  <td><a href="${ctx}/admin/video/edit?id=${v.id}">Sửa</a> · <a class="danger-link" href="${ctx}/admin/video/delete?id=${v.id}">Xóa</a></td>
</tr></c:forEach>
<c:if test="${empty videoList}"><tr><td colspan="7" class="empty">Không có video. Hãy thêm video mới.</td></tr></c:if>
</tbody></table></div><p class="muted">Servlet Controller → Service → DAO (JPA/Hibernate) → SQL Server · quan hệ [User] 1-n Videos</p>
</main></body></html>
