<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header class="app-header navbar bg-white border-bottom">
  <div class="container-xl app-nav-container">
    <a class="brand navbar-brand" href="${ctx}/home">Shop<span>24162039</span></a>
    <nav class="app-nav nav" aria-label="Điều hướng chính">
      <a class="nav-link" href="${ctx}/home">Trang chủ</a>
      <a class="nav-link" href="${ctx}/products">Sản phẩm</a>
      <a class="nav-link" href="${ctx}/video">Video</a>
      <c:choose>
        <c:when test="${not empty sessionScope.account}">
          <a class="nav-link" href="${ctx}/video/upload">Tải video</a>
          <c:if test="${sessionScope.account.roleid == 1 or sessionScope.account.roleid == 2}">
            <a class="nav-link" href="${ctx}/dashboard">Dashboard</a>
          </c:if>
          <span class="who"><c:out value="${sessionScope.account.fullName}" /></span>
          <form action="${ctx}/logout" method="post" class="inline">
            <input type="hidden" name="csrf" value="${sessionScope.csrf}">
            <button class="link-button" type="submit">Đăng xuất</button>
          </form>
        </c:when>
        <c:otherwise>
          <a class="nav-link" href="${ctx}/login">Đăng nhập</a>
          <a class="nav-link" href="${ctx}/register">Đăng ký</a>
        </c:otherwise>
      </c:choose>
    </nav>
  </div>
</header>
