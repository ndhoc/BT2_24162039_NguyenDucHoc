<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<fmt:setLocale value="vi_VN"/>
<!doctype html>
<html><head><title>Dashboard | Shop 24162039</title></head><body>
<main class="page">
  <div class="section-head">
    <div><p class="eyebrow">Khu vực quản trị</p><h1>Dashboard</h1></div>
    <div class="hero-actions dashboard-actions">
      <c:if test="${sessionScope.account.roleid == 1}"><a class="button secondary" href="${ctx}/admin/product/list">Quản lý sản phẩm</a></c:if>
      <c:if test="${sessionScope.account.roleid == 1}"><a class="button secondary" href="${ctx}/admin/video/list">Quản lý video</a></c:if>
      <c:if test="${sessionScope.account.roleid == 1}"><a class="button secondary" href="${ctx}/admin/category/list">Quản lý danh mục</a></c:if>
      <a class="button secondary" href="${ctx}/products">Xem cửa hàng</a>
    </div>
  </div>

  <div class="stat-grid">
    <div class="stat"><span>Sản phẩm</span><strong>${productCount}</strong></div>
    <div class="stat"><span>Danh mục</span><strong>${categoryCount}</strong></div>
    <div class="stat"><span>Người dùng</span><strong>${userCount}</strong></div>
    <div class="stat"><span>Tổng giá trị sản phẩm</span><strong><fmt:formatNumber value="${totalValue}" type="number" maxFractionDigits="2"/> ₫</strong></div>
  </div>

  <div class="dashboard-grid">
    <section class="panel">
      <h2>Sản phẩm theo danh mục</h2>
      <table>
        <thead><tr><th>Danh mục</th><th>Số sản phẩm</th></tr></thead>
        <tbody>
        <c:forEach items="${categoryStats}" var="row">
          <tr><td><c:out value="${row[0]}"/></td><td>${row[1]}</td></tr>
        </c:forEach>
        <c:if test="${empty categoryStats}"><tr><td colspan="2" class="muted">Chưa có dữ liệu.</td></tr></c:if>
        </tbody>
      </table>
    </section>

    <section class="panel">
      <h2>Sản phẩm mới nhất</h2>
      <table>
        <thead><tr><th>Ảnh</th><th>Sản phẩm</th><th>Danh mục</th><th>Giá</th></tr></thead>
        <tbody>
        <c:forEach items="${recentProducts}" var="p">
          <tr>
            <td><c:if test="${not empty p.image}"><img class="thumb" src="${ctx}/<c:out value='${p.image}'/>" alt=""></c:if></td>
            <td><a href="${ctx}/products/detail?id=${p.id}"><c:out value="${p.name}"/></a></td>
            <td><c:out value="${p.category.name}"/></td>
            <td class="nowrap"><fmt:formatNumber value="${p.price}" type="number" maxFractionDigits="2"/> ₫</td>
          </tr>
        </c:forEach>
        <c:if test="${empty recentProducts}"><tr><td colspan="4" class="muted">Chưa có sản phẩm.</td></tr></c:if>
        </tbody>
      </table>
    </section>
  </div>
</main>
</body></html>
