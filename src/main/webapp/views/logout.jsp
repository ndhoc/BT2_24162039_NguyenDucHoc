<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html><head><title>Đăng xuất</title></head><body><main class="card auth"><h1>Đăng xuất</h1><p>Kết thúc phiên làm việc và xóa Cookie ghi nhớ đăng nhập?</p><form method="post" action="${pageContext.request.contextPath}/logout"><input type="hidden" name="csrf" value="${sessionScope.csrf}"><button>Đăng xuất</button></form><p><a href="${pageContext.request.contextPath}/">Quay lại</a></p></main></body></html>
