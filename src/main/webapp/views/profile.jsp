<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!doctype html>
<html>
<head><title>Hồ sơ cá nhân | Shop 24162039</title></head>
<body>
<main class="page profile-page">
  <h1>Hồ sơ cá nhân</h1>
  <c:choose>
    <c:when test="${param.saved eq 'profile'}"><p class="success" role="status">Đã lưu hồ sơ.</p></c:when>
    <c:when test="${param.saved eq 'email'}"><p class="success" role="status">Đã xác nhận và cập nhật email.</p></c:when>
    <c:when test="${param.saved eq 'otp' and otpViaEmail}"><p class="success" role="status">Đã gửi mã OTP tới email mới.</p></c:when>
    <c:when test="${param.saved eq 'otp'}"><p class="success" role="status">Mã OTP đã được ghi vào nhật ký phát triển.</p></c:when>
  </c:choose>
  <c:if test="${not empty alert}"><p class="alert" role="alert"><c:out value="${alert}"/></p></c:if>

  <div class="profile-grid">
    <section class="card" aria-labelledby="profile-details">
      <h2 id="profile-details">Thông tin cá nhân</h2>
      <c:set var="defaultAvatar" value="${ctx}/images/default-avatar.png" />
      <img class="profile-avatar" width="96" height="96" alt="Ảnh đại diện của bạn"
           src="<c:out value='${empty profileAccount.avatar ? defaultAvatar : profileAccount.avatar}'/>"
           data-avatar-fallback="${defaultAvatar}">
      <form action="${ctx}/profile" method="post" enctype="multipart/form-data">
        <input type="hidden" name="csrf" value="${sessionScope.csrf}">
        <input type="hidden" name="action" value="save-profile">

        <label for="fullname">Họ và tên</label>
        <input class="form-control" id="fullname" name="fullname" maxlength="100" autocomplete="name" data-nonblank required
               aria-invalid="${not empty fieldErrors.fullname}" aria-describedby="fullname-error"
               value="<c:out value='${profileSubmitted ? param.fullname : profileAccount.fullName}'/>">
        <form:field-error field="fullname"/>

        <label for="phone">Số điện thoại</label>
        <input class="form-control" id="phone" name="phone" type="tel" maxlength="15" pattern="[+]?[0-9]{9,15}" autocomplete="tel" required
               aria-invalid="${not empty fieldErrors.phone}" aria-describedby="phone-error"
               value="<c:out value='${profileSubmitted ? param.phone : profileAccount.phone}'/>">
        <form:field-error field="phone"/>

        <label for="avatar">Ảnh đại diện</label>
        <input class="form-control" id="avatar" type="file" name="avatar" accept="image/png,image/jpeg" data-max-mb="5"
               aria-invalid="${not empty fieldErrors.avatar}" aria-describedby="avatar-hint avatar-error">
        <small class="muted" id="avatar-hint">PNG hoặc JPEG, tối đa 5 MB.</small>
        <form:field-error field="avatar"/>

        <button class="btn btn-primary" type="submit">Lưu hồ sơ</button>
      </form>
    </section>

    <section class="card" aria-labelledby="profile-email">
      <h2 id="profile-email">Đổi email</h2>
      <p>Email hiện tại <strong data-current-email><c:out value="${profileAccount.email}"/></strong></p>
      <form action="${ctx}/profile" method="post">
        <input type="hidden" name="csrf" value="${sessionScope.csrf}">
        <input type="hidden" name="action" value="request-email">
        <label for="new-email">Email mới</label>
        <input class="form-control" id="new-email" name="email" type="email" maxlength="255" autocomplete="email" required
               pattern="[^\s@]+@[^\s@]+\.[^\s@]+"
               aria-invalid="${not empty fieldErrors.email}" aria-describedby="email-error"
               value="<c:out value='${param.action eq "request-email" ? param.email : pendingEmail.email}'/>">
        <form:field-error field="email"/>
        <button class="${empty pendingEmail ? 'btn btn-primary' : 'btn btn-outline-primary'}" type="submit">${empty pendingEmail ? 'Gửi mã OTP' : 'Gửi lại mã OTP'}</button>
      </form>
      <c:if test="${not empty pendingEmail}">
        <form class="profile-otp" action="${ctx}/profile" method="post">
          <input type="hidden" name="csrf" value="${sessionScope.csrf}">
          <input type="hidden" name="action" value="confirm-email">
          <p>Đang chờ xác nhận <strong><c:out value="${pendingEmail.email}"/></strong></p>
          <div class="otp-field" data-otp>
            <label for="profile-code">Mã OTP</label>
            <input class="otp-fallback" id="profile-code" name="code" inputmode="numeric" pattern="[0-9]{6}"
                   maxlength="6" autocomplete="one-time-code" required
                   aria-invalid="${not empty fieldErrors.code}" aria-describedby="code-error">
          </div>
          <form:field-error field="code"/>
          <small class="muted">Mã gồm 6 chữ số, có hiệu lực trong 10 phút.</small>
          <button class="btn btn-primary" type="submit">Xác nhận email mới</button>
        </form>
      </c:if>
    </section>
  </div>
</main>
<script src="${ctx}/js/otp-input.js?v=bt3" defer></script>
</body>
</html>
