<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header class="app-header navbar bg-white border-bottom">
  <div class="container-xl app-nav-container">
    <a class="brand navbar-brand" href="${ctx}/home">Shop<span>24162039</span></a>
    <button class="nav-toggle" type="button" aria-expanded="false" aria-controls="main-navigation" hidden>Menu</button>
    <nav class="app-nav nav" id="main-navigation" aria-label="Điều hướng chính">
      <a class="nav-link" href="${ctx}/home">Trang chủ</a>
      <a class="nav-link" href="${ctx}/products">Sản phẩm</a>
      <a class="nav-link" href="${ctx}/video">Video</a>
      <c:choose>
        <c:when test="${not empty sessionScope.account}">
          <a class="nav-link" href="${ctx}/video/upload">Tải video</a>
          <c:if test="${sessionScope.account.roleid == 1 or sessionScope.account.roleid == 2}">
            <a class="nav-link" href="${ctx}/dashboard">Dashboard</a>
          </c:if>
          <c:set var="defaultAvatar" value="https://res.cloudinary.com/djr3qad5v/image/upload/v1795144636/shop-24162039/site-assets/default-avatar-6918b9f64af3540f.png" />
          <a class="nav-link account-link" href="${ctx}/profile" aria-label="Hồ sơ của <c:out value='${sessionScope.account.fullName}'/>">
            <img class="account-avatar" width="36" height="36" alt=""
                 src="<c:out value='${empty sessionScope.account.avatar ? defaultAvatar : sessionScope.account.avatar}'/>"
                 data-avatar-fallback="${defaultAvatar}">
            <span class="account-name"><c:out value="${sessionScope.account.fullName}" /></span>
          </a>
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
