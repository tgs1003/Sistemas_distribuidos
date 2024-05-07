
<%
	if (request.getParameter("nombre_usuario").isEmpty() == true) {
%>

<%@ include file="index.html"%>

<%
	} else {
%>

<jsp:forward page="salaChat.jsp" />

<%
	}
%>