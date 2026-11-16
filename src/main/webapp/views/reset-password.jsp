<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Đặt lại mật khẩu | 24162039</title><link rel="stylesheet" href="${ctx}/css/style.css?v=20261116-2"></head><body>
<main class="card auth">
<p class="eyebrow">24162039 · Nguyễn Đức Học</p><h1>Đặt lại mật khẩu</h1>
<p>Mã xác nhận đã được gửi tới <span class="otp-recipient"><c:out value="${maskedEmail}"/></span> cho tài khoản <strong><c:out value="${username}"/></strong>.</p>
<c:if test="${param.sent == '1'}"><p class="success">Mã xác nhận đã được gửi. Vui lòng kiểm tra email.</p></c:if>
<c:if test="${not empty message}"><p class="success"><c:out value="${message}"/></p></c:if>
<c:if test="${not empty alert}"><p class="alert" role="alert"><c:out value="${alert}"/></p></c:if>

<c:choose>
  <c:when test="${step == 'password'}">
    <p><span class="otp-step done">✓ Đã xác nhận mã OTP</span></p>
    <form action="${ctx}/reset-password" method="post">
      <input type="hidden" name="csrf" value="${sessionScope.csrf}">
      <input type="hidden" name="action" value="set-password">
      <label for="password">Mật khẩu mới</label>
      <input id="password" type="password" name="password" minlength="6" maxlength="128" autocomplete="new-password" required>
      <label for="confirm">Xác nhận mật khẩu mới</label>
      <input id="confirm" type="password" name="confirm" minlength="6" maxlength="128" autocomplete="new-password" required>
      <button type="submit">Đổi mật khẩu</button>
    </form>
  </c:when>
  <c:otherwise>
    <p><span class="otp-step">Bước 1 · Nhập mã OTP</span></p>
    <form action="${ctx}/reset-password" method="post">
      <input type="hidden" name="csrf" value="${sessionScope.csrf}">
      <input type="hidden" name="action" value="verify-code">
      <div class="otp-field" data-otp>
        <label for="code-0">Mã xác nhận OTP</label>
        <input id="code-0" class="otp-fallback" name="code" inputmode="numeric" pattern="[0-9]{6}" maxlength="6" autocomplete="one-time-code" placeholder="123456" required>
      </div>
      <p class="otp-hint">Nhập 6 chữ số trong email. Mã có hiệu lực 10 phút.</p>
      <button type="submit">Xác nhận mã</button>
    </form>
    <form action="${ctx}/reset-password" method="post" class="inline">
      <input type="hidden" name="csrf" value="${sessionScope.csrf}">
      <input type="hidden" name="action" value="resend">
      <button type="submit" class="secondary">Gửi lại mã OTP</button>
    </form>
  </c:otherwise>
</c:choose>

<p class="auth-footer"><a href="${ctx}/forgot-password">Dùng email khác</a> <a href="${ctx}/login">Đăng nhập</a></p>
</main>
<script src="${ctx}/js/otp-input.js?v=20261116-1"></script>
</body></html>
