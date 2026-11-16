<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Quên mật khẩu | 24162039</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=20261116-1"></head><body>
<main class="card auth">
<p class="eyebrow">24162039 · Nguyễn Đức Học</p><h1>Quên mật khẩu</h1>
<p>Nhập email đã đăng ký. Hệ thống sẽ gửi mã xác nhận OTP gồm 6 chữ số có hiệu lực 10 phút.</p>
<c:if test="${param.expired == '1'}"><p class="alert" role="alert">Phiên đặt lại mật khẩu đã hết. Vui lòng nhập lại email.</p></c:if>
<c:if test="${not empty alert}"><p class="alert" role="alert"><c:out value="${alert}"/></p></c:if>
<form action="${pageContext.request.contextPath}/forgot-password" method="post">
  <input type="hidden" name="csrf" value="${sessionScope.csrf}">
  <label for="email">Email</label>
  <input id="email" type="email" name="email" maxlength="255" value="<c:out value='${email}'/>" autocomplete="email" required>
  <button type="submit">Gửi mã xác nhận</button>
</form>
<p class="auth-footer"><a href="${pageContext.request.contextPath}/login">Quay lại đăng nhập</a> <a href="${pageContext.request.contextPath}/register">Đăng ký</a></p>
<p class="muted">Kiểm thử cục bộ: khi chưa cấu hình SMTP, mã OTP được ghi vào <code>logs/otp-dev.log</code> của Tomcat.</p>
</main></body></html>
