package es.ubu.lsi.Sesion8;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ServletEjemplo")
public class ServletEjemplo extends HttpServlet{
	private static final long serialVersionUID = -1376319434605897718L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		//Establecer el tipo de contenido de la respuesta
		response.setContentType("text/html");
		
		//Obtener los parámetros del formulario
		String campo1 = request.getParameter("campo1");
		String campo2 = request.getParameter("campo2");
		String campo3 = request.getParameter("campo3");
		
		//Crear el contenido de la respuesta
		PrintWriter out = response.getWriter();
		out.println("<html><head><title>Eco del formulario</title></head><body>");
		out.println("<h2>Valores recibidos:</h2>");
		out.println("<p>Campo 1: " + campo1 + "</p>");
		out.println("<p>Campo 2: " + campo2 + "</p>");
		out.println("<p>Campo 3: " + campo3 + "</p>");
		out.println("</body></html>");
		}
}