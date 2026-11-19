<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html><head><title>Xóa danh mục | 24162039</title></head><body><main class="card auth"><h1>Xóa danh mục</h1><p>Bạn có muốn xóa danh mục <strong><c:out value="${category.name}"/></strong>?</p><c:if test="${not empty alert}"><p class="alert"><c:out value="${alert}"/></p></c:if><form action="${pageContext.request.contextPath}/admin/category/delete" method="post"><input type="hidden" name="csrf" value="${sessionScope.csrf}"><input type="hidden" name="id" value="${category.id}"><button class="danger" type="submit">Xác nhận xóa</button><a href="${pageContext.request.contextPath}/admin/category/list">Hủy</a></form></main></body></html>
