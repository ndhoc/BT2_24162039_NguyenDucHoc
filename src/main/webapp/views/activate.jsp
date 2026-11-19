<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!doctype html>
<html><head><title>Kích hoạt tài khoản | 24162039</title></head><body>
<main class="card auth">
<p class="eyebrow">24162039 · Nguyễn Đức Học</p><h1>Kích hoạt tài khoản</h1>
<c:choose>
  <c:when test="${lost}">
    <p class="alert" role="alert">Không tìm thấy tài khoản đang chờ kích hoạt. Hãy đăng nhập để nhận mã mới hoặc đăng ký lại.</p>
    <p class="auth-footer"><a href="${ctx}/login">Đăng nhập</a> <a href="${ctx}/register">Đăng ký</a></p>
  </c:when>
  <c:otherwise>
    <p>Mã OTP gồm 6 chữ số đã được gửi tới <span class="otp-recipient"><c:out value="${maskedEmail}"/></span> cho tài khoản <strong><c:out value="${username}"/></strong>.</p>
    <c:if test="${param.sent == '1'}"><p class="success">Đăng ký thành công. Vui lòng nhập mã OTP trong email để kích hoạt.</p></c:if>
    <c:if test="${not empty message}"><p class="success"><c:out value="${message}"/></p></c:if>
    <c:if test="${not empty alert}"><p class="alert" role="alert"><c:out value="${alert}"/></p></c:if>
    <form action="${ctx}/activate" method="post">
      <input type="hidden" name="csrf" value="${sessionScope.csrf}">
      <div class="otp-field" data-otp>
        <label for="code-0">Mã OTP</label>
        <input id="code-0" class="otp-fallback" name="code" aria-invalid="${not empty fieldErrors.code}" aria-describedby="code-error" inputmode="numeric" pattern="[0-9]{6}" maxlength="6" autocomplete="one-time-code" placeholder="123456" required>
        <form:field-error field="code" />
      </div>
      <p class="otp-hint">Nhập 6 chữ số trong email. Mã có hiệu lực 10 phút.</p>
      <button class="btn btn-primary" type="submit">Kích hoạt</button>
    </form>
    <form action="${ctx}/activate" method="post" class="inline">
      <input type="hidden" name="csrf" value="${sessionScope.csrf}">
      <input type="hidden" name="action" value="resend">
      <button type="submit" class="secondary">Gửi lại mã OTP</button>
    </form>
    <p class="auth-footer"><a href="${ctx}/login">Quay lại đăng nhập</a></p>
  </c:otherwise>
</c:choose>
<p class="muted">Kiểm thử cục bộ: khi chưa cấu hình SMTP, mã OTP được ghi vào <code>logs/otp-dev.log</code> của Tomcat.</p>
</main>
<script src="${ctx}/js/otp-input.js?v=bt3"></script>
</body></html>
