<%@page import="java.util.List"%>
<%@page import="es.ubu.lsi.Practica3ChatWeb.Chat"%>
<%
	String client = (String) session.getAttribute("client");
	Chat server = (Chat) application.getAttribute("server");
	List<String> messages = server.obtener_mensajes(client);
	for (String message : messages) {
		out.print(message + "<br/>");
	}
%>