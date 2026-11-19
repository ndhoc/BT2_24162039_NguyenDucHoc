<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html><head><title>Thông báo</title></head><body><main class="card auth"><h1>Không thể hoàn tất yêu cầu</h1><p class="alert"><c:out value="${errorMessage}" default="Đã xảy ra lỗi. Vui lòng thử lại."/></p><p><a href="${pageContext.request.contextPath}/login">Đăng nhập</a> · <a href="${pageContext.request.contextPath}/">Trang chủ</a></p></main></body></html>
