<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!doctype html>
<html><head><title>Đặt lại mật khẩu | 24162039</title></head><body>
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
      <input id="password" type="password" name="password" aria-invalid="${not empty fieldErrors.password}" aria-describedby="password-error" class="form-control${empty fieldErrors.password ? '' : ' is-invalid'}" data-nonblank minlength="6" maxlength="128" autocomplete="new-password" required>
      <form:field-error field="password" />
      <label for="confirm">Xác nhận mật khẩu mới</label>
      <input id="confirm" type="password" name="confirm" aria-invalid="${not empty fieldErrors.confirm}" aria-describedby="confirm-error" class="form-control${empty fieldErrors.confirm ? '' : ' is-invalid'}" data-match="password" minlength="6" maxlength="128" autocomplete="new-password" required>
      <form:field-error field="confirm" />
      <button class="btn btn-primary" type="submit">Đổi mật khẩu</button>
    </form>
  </c:when>
  <c:otherwise>
    <p><span class="otp-step">Bước 1 · Nhập mã OTP</span></p>
    <form action="${ctx}/reset-password" method="post">
      <input type="hidden" name="csrf" value="${sessionScope.csrf}">
      <input type="hidden" name="action" value="verify-code">
      <div class="otp-field" data-otp>
        <label for="code-0">Mã xác nhận OTP</label>
        <input id="code-0" class="otp-fallback" name="code" aria-invalid="${not empty fieldErrors.code}" aria-describedby="code-error" inputmode="numeric" pattern="[0-9]{6}" maxlength="6" autocomplete="one-time-code" placeholder="123456" required>
        <form:field-error field="code" />
      </div>
      <p class="otp-hint">Nhập 6 chữ số trong email. Mã có hiệu lực 10 phút.</p>
      <button class="btn btn-primary" type="submit">Xác nhận mã</button>
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
<script src="${ctx}/js/otp-input.js?v=bt3"></script>
</body></html>
