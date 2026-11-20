<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!doctype html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><sitemesh:write property="title" default="Shop 24162039" /></title>
  <link rel="stylesheet" href="${ctx}/templates/bootstrap/bootstrap.min.css?v=5.3.9">
  <link rel="stylesheet" href="${ctx}/css/style.css?v=profile-avatar">
  <link rel="stylesheet" href="${ctx}/css/storefront.css?v=1">
  <sitemesh:write property="head" />
</head>
<body class="app-layout d-flex flex-column min-vh-100" data-layout="${adminLayout ? 'admin' : 'web'}">
  <a class="app-skip-link" href="#page-content">Đến nội dung chính</a>
  <%@ include file="/common/web/header.jsp" %>
  <div class="container-xl app-content flex-grow-1 ${adminLayout and sessionScope.account.roleid == 1 ? 'app-admin-grid' : ''}">
    <c:if test="${adminLayout}">
      <%@ include file="/common/admin/left.jsp" %>
    </c:if>
    <div class="app-page" id="page-content" tabindex="-1">
      <sitemesh:write property="body" />
    </div>
  </div>
  <%@ include file="/common/web/footer.jsp" %>
  <script src="${ctx}/js/form-validation.js?v=bt3" defer></script>
  <script src="${ctx}/js/avatar.js?v=1" defer></script>
  <script src="${ctx}/js/navigation.js?v=1" defer></script>
</body>
</html>
