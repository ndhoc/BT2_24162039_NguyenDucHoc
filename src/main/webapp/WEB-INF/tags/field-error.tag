<%@ tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ attribute name="field" required="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<div id="${field}-error" class="invalid-feedback field-feedback" aria-live="polite"><c:out value="${requestScope.fieldErrors[field]}" /></div>
