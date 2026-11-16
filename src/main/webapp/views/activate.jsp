<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Kích hoạt tài khoản | 24162039</title><link rel="stylesheet" href="${ctx}/css/style.css?v=20261116-2"></head><body>
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
        <input id="code-0" class="otp-fallback" name="code" inputmode="numeric" pattern="[0-9]{6}" maxlength="6" autocomplete="one-time-code" placeholder="123456" required>
      </div>
      <p class="otp-hint">Nhập 6 chữ số trong email. Mã có hiệu lực 10 phút.</p>
      <button type="submit">Kích hoạt</button>
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
<script src="${ctx}/js/otp-input.js?v=20261116-1"></script>
</body></html>
