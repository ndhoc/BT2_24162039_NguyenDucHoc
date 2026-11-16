<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Kích hoạt tài khoản | 24162039</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=20261110-2"></head><body>
<main class="card auth">
<p class="eyebrow">24162039 · Nguyễn Đức Học</p><h1>Kích hoạt tài khoản</h1>
<c:choose>
  <c:when test="${lost}">
    <p class="alert" role="alert">Không tìm thấy tài khoản đang chờ kích hoạt. Hãy đăng nhập để nhận mã mới hoặc đăng ký lại.</p>
    <p class="auth-footer"><a href="${pageContext.request.contextPath}/login">Đăng nhập</a> <a href="${pageContext.request.contextPath}/register">Đăng ký</a></p>
  </c:when>
  <c:otherwise>
    <p>Mã OTP gồm 6 chữ số đã được gửi tới <strong><c:out value="${maskedEmail}"/></strong> cho tài khoản <strong><c:out value="${username}"/></strong>. Mã có hiệu lực 10 phút.</p>
    <c:if test="${param.sent == '1'}"><p class="success">Đăng ký thành công. Vui lòng nhập mã OTP trong email để kích hoạt.</p></c:if>
    <c:if test="${not empty message}"><p class="success"><c:out value="${message}"/></p></c:if>
    <c:if test="${not empty alert}"><p class="alert" role="alert"><c:out value="${alert}"/></p></c:if>
    <form action="${pageContext.request.contextPath}/activate" method="post">
      <input type="hidden" name="csrf" value="${sessionScope.csrf}">
      <label for="code">Mã OTP</label>
      <input id="code" name="code" inputmode="numeric" pattern="[0-9]{6}" maxlength="6" autocomplete="one-time-code" placeholder="123456" required>
      <button type="submit">Kích hoạt</button>
    </form>
    <form action="${pageContext.request.contextPath}/activate" method="post" class="inline">
      <input type="hidden" name="csrf" value="${sessionScope.csrf}">
      <input type="hidden" name="action" value="resend">
      <button type="submit" class="secondary">Gửi lại mã OTP</button>
    </form>
    <p class="auth-footer"><a href="${pageContext.request.contextPath}/login">Quay lại đăng nhập</a></p>
  </c:otherwise>
</c:choose>
<p class="muted">Kiểm thử cục bộ: khi chưa cấu hình SMTP, mã OTP được ghi vào <code>logs/otp-dev.log</code> của Tomcat.</p>
</main></body></html>
