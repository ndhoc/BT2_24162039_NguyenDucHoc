<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!doctype html>
<html>
<head><title>Xóa sản phẩm | 24162039</title></head>
<body>
<main class="card auth">
  <h1>Xóa sản phẩm</h1>
  <p>Bạn có muốn xóa sản phẩm <strong><c:out value="${product.name}"/></strong>?</p>
  <form action="${ctx}/admin/product/delete" method="post">
    <input type="hidden" name="csrf" value="${sessionScope.csrf}">
    <input type="hidden" name="id" value="${product.id}">
    <button class="btn btn-danger" type="submit">Xác nhận xóa</button>
    <a href="${ctx}/admin/product/list">Hủy</a>
  </form>
</main>
</body>
</html>
