<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags" %>
<!doctype html>
<html><head><title>Đăng ký | 24162039</title></head><body><main class="card auth">
<p class="eyebrow">24162039 · Nguyễn Đức Học</p><h1>Tạo tài khoản</h1><p>Nhập thông tin để đăng ký. Sau khi đăng ký, nhập mã OTP gửi tới email để kích hoạt tài khoản trước khi đăng nhập.</p>
<c:if test="${not empty alert}"><p class="alert" role="alert"><c:out value="${alert}"/></p></c:if>
<form action="${pageContext.request.contextPath}/register" method="post"><input type="hidden" name="csrf" value="${sessionScope.csrf}">
<label for="fullname">Họ và tên</label><input id="fullname" name="fullname" aria-invalid="${not empty fieldErrors.fullname}" aria-describedby="fullname-error" class="form-control${empty fieldErrors.fullname ? '' : ' is-invalid'}" data-nonblank maxlength="100" value="<c:out value='${param.fullname}'/>" autocomplete="name" required>
<form:field-error field="fullname" />
<label for="username">Tên đăng nhập</label><input id="username" name="username" aria-invalid="${not empty fieldErrors.username}" aria-describedby="username-error" class="form-control${empty fieldErrors.username ? '' : ' is-invalid'}" minlength="3" maxlength="50" pattern="[A-Za-z0-9_]{3,50}" title="3–50 chữ cái, chữ số hoặc dấu gạch dưới" value="<c:out value='${param.username}'/>" autocomplete="username" required>
<form:field-error field="username" />
<label for="email">Email</label><input id="email" type="email" name="email" aria-invalid="${not empty fieldErrors.email}" aria-describedby="email-error" class="form-control${empty fieldErrors.email ? '' : ' is-invalid'}" pattern="[^\s@]+@[^\s@]+\.[^\s@]+" maxlength="255" value="<c:out value='${param.email}'/>" autocomplete="email" required>
<form:field-error field="email" />
<label for="phone">Số điện thoại</label><input id="phone" name="phone" aria-invalid="${not empty fieldErrors.phone}" aria-describedby="phone-error" class="form-control${empty fieldErrors.phone ? '' : ' is-invalid'}" pattern="[+]?[0-9]{9,15}" title="Ít nhất 9 chữ số, tối đa 15 ký tự kể cả dấu + ở đầu" type="tel" maxlength="15" value="<c:out value='${param.phone}'/>" autocomplete="tel" required>
<form:field-error field="phone" />
<label for="password">Mật khẩu</label><input id="password" type="password" name="password" aria-invalid="${not empty fieldErrors.password}" aria-describedby="password-error" class="form-control${empty fieldErrors.password ? '' : ' is-invalid'}" data-nonblank minlength="6" maxlength="128" autocomplete="new-password" required>
<form:field-error field="password" />
<label for="confirm">Xác nhận mật khẩu</label><input id="confirm" type="password" name="confirm" aria-invalid="${not empty fieldErrors.confirm}" aria-describedby="confirm-error" class="form-control${empty fieldErrors.confirm ? '' : ' is-invalid'}" data-match="password" minlength="6" maxlength="128" autocomplete="new-password" required>
<form:field-error field="confirm" />
<button class="btn btn-primary" type="submit">Đăng ký</button></form><p class="auth-footer"><a href="${pageContext.request.contextPath}/login">Quay lại đăng nhập</a> <a href="${pageContext.request.contextPath}/forgot-password">Quên mật khẩu?</a></p>
</main></body></html>
