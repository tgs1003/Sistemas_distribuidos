<%@page import="es.ubu.lsi.Practica3ChatWeb.Chat"%>
<%@page import="java.lang.String"%>

<jsp:useBean id="server" scope="application"
	class="es.ubu.lsi.Practica3ChatWeb.Chat" />
<jsp:useBean id="client" scope="session" class="java.lang.String" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html class="htmlCenter">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta http-equiv="refresh" content="5;url=index.html" />
<title>Logout</title>
<link rel="stylesheet" type="text/css" href="./css/style.css">
</head>
<body>
	<%
		client = (String) session.getAttribute("client");
		System.out.println("El cliente '" + client + "' se desconectar�.");
		server.baja_usuario(client);
		session.invalidate();
	%>
	<h1>Te has deslogueado</h1>
	<p>Ser�s redirigido a la p�gina principal en 5 segundos.</p>

</body>
</html>