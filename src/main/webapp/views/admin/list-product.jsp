<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<fmt:setLocale value="vi_VN"/>
<!doctype html>
<html>
<head><title>Quản lý sản phẩm | 24162039</title></head>
<body>
<main class="card wide">
  <header>
    <h1>Quản lý sản phẩm</h1>
    <a class="btn btn-primary" href="${ctx}/admin/product/add">Thêm sản phẩm</a>
  </header>
  <c:choose>
    <c:when test="${param.saved eq 'add'}"><p class="success" role="status">Đã thêm sản phẩm.</p></c:when>
    <c:when test="${param.saved eq 'edit'}"><p class="success" role="status">Đã lưu thay đổi.</p></c:when>
    <c:when test="${param.saved eq 'delete'}"><p class="success" role="status">Đã xóa sản phẩm.</p></c:when>
  </c:choose>
  <form class="search" action="${ctx}/admin/product/list" method="get">
    <label for="q">Tìm theo tên sản phẩm</label>
    <input id="q" name="q" value="<c:out value='${param.q}'/>">
    <button class="btn btn-primary" type="submit">Tìm kiếm</button>
    <c:if test="${not empty param.q}"><a href="${ctx}/admin/product/list">Xóa bộ lọc</a></c:if>
  </form>
  <div class="table-wrap">
    <table>
      <thead>
        <tr><th scope="col">Ảnh</th><th scope="col">Sản phẩm</th><th scope="col">Danh mục</th>
          <th scope="col">Giá</th><th scope="col">Thao tác</th></tr>
      </thead>
      <tbody>
        <c:forEach items="${productList}" var="p">
          <tr>
            <td>
              <c:if test="${not empty p.image}">
                <img class="thumb" src="${ctx}/<c:out value='${p.image}'/>" alt="" loading="lazy">
              </c:if>
            </td>
            <td><a href="${ctx}/products/detail?id=${p.id}"><c:out value="${p.name}"/></a></td>
            <td><c:out value="${p.category.name}"/></td>
            <td class="nowrap"><fmt:formatNumber value="${p.price}" type="number" maxFractionDigits="2"/> ₫</td>
            <td class="nowrap">
              <a href="${ctx}/admin/product/edit?id=${p.id}">Sửa</a> ·
              <a class="danger-link" href="${ctx}/admin/product/delete?id=${p.id}">Xóa</a>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty productList}"><tr><td colspan="5" class="empty">Không tìm thấy sản phẩm.</td></tr></c:if>
      </tbody>
    </table>
  </div>
</main>
</body>
</html>
