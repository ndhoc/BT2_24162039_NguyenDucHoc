<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<footer class="app-footer border-top">
  <div class="container-xl app-footer-container">
    <div class="app-footer-main">
      <div class="app-footer-intro">
        <a class="app-footer-brand" href="${ctx}/home">Shop<span>24162039</span></a>
        <p>Điện thoại, máy tính và sách.</p>
      </div>
      <nav class="app-footer-nav" aria-labelledby="footer-explore">
        <h2 id="footer-explore">Khám phá</h2>
        <ul>
          <li><a href="${ctx}/home">Trang chủ</a></li>
          <li><a href="${ctx}/products">Sản phẩm</a></li>
          <li><a href="${ctx}/video">Thư viện video</a></li>
        </ul>
      </nav>
      <nav class="app-footer-nav" aria-labelledby="footer-account">
        <h2 id="footer-account">Tài khoản</h2>
        <ul>
          <c:choose>
            <c:when test="${empty sessionScope.account}">
              <li><a href="${ctx}/login">Đăng nhập</a></li>
              <li><a href="${ctx}/register">Đăng ký</a></li>
              <li><a href="${ctx}/forgot-password">Quên mật khẩu</a></li>
            </c:when>
            <c:otherwise>
              <li><a href="${ctx}/video/upload">Tải video</a></li>
              <c:if test="${sessionScope.account.roleid == 1 or sessionScope.account.roleid == 2}">
                <li><a href="${ctx}/dashboard">Dashboard</a></li>
              </c:if>
              <li><a href="${ctx}/logout">Đăng xuất</a></li>
            </c:otherwise>
          </c:choose>
        </ul>
      </nav>
    </div>
    <div class="app-footer-bottom">
      <p>© 2026 Shop24162039</p>
      <p>Nguyễn Đức Học · MSSV 24162039</p>
    </div>
  </div>
</footer>
