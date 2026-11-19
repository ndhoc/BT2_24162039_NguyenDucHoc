<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:if test="${sessionScope.account.roleid == 1}">
  <nav class="app-admin-nav nav nav-pills" aria-label="Quản trị">
    <a class="nav-link" href="${ctx}/dashboard">Tổng quan</a>
    <a class="nav-link" href="${ctx}/admin/product/list">Quản lý sản phẩm</a>
    <a class="nav-link" href="${ctx}/admin/category/list">Quản lý danh mục</a>
    <a class="nav-link" href="${ctx}/admin/video/list">Quản lý video</a>
  </nav>
</c:if>
