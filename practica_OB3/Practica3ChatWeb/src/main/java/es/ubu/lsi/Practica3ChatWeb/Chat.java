package es.ubu.lsi.Practica3ChatWeb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementación del chat en el servidor
 * 
 * @author Teodoro Ricardo García Sánchez
 * 
 */
public class Chat{
	
	//Mensajes de los usuarios
	private HashMap<String, List<String>> mensajes = new HashMap<String, List<String>>();
	
	//Usuarios y sus baneos
	private HashMap<String, HashSet<String>> baneos = new HashMap<String, HashSet<String>>();

	public Chat() { 
		
	}
	
	/**
	 * Da de alta un usuario en el chat
	 * @param usuario
	 */
	public void alta_usuario(String usuario)
	{
		mensajes.put(usuario, new ArrayList<String>());
		baneos.put(usuario, new HashSet<String>());
		enviar_mensaje("El usuario " + usuario +" se ha conectado.","Administrador");
	}
	
	/**
	 * Devuelve los mensajes de un usuario
	 * @param usuario Usuario del que se quiere obtener los mensajes.
	 * @return La lista de mensajes
	 */
	public List<String> obtener_mensajes(String usuario)
	{
		return mensajes.get(usuario);
	}
	
	/**
	 * Método para enviar un mensaje.
	 * @param texto Texto del mensaje.
	 * @param usuario Usuario al que se le envía el mensaje.
	 */
	public void enviar_mensaje(String texto, String usuario)
	{
		Set<String> usuarios = mensajes.keySet();
		List<String> mensajes_usuario = null;
		for (String usuario_envio : usuarios) {
			mensajes_usuario = mensajes.get(usuario_envio);
			if(usuario.equals(mensajes_usuario)) {
				mensajes_usuario.add("Yo: " + texto);
				mensajes.put(usuario_envio, mensajes_usuario);
			} else if (!esta_baneado(usuario_envio, usuario)) {
				mensajes_usuario.add(usuario + ": " + texto);
				mensajes.put(usuario_envio, mensajes_usuario);
			}
		}
	}
	
	/**
	 * Método para dar de baja un usuario.
	 * @param usuario Usuario a dar de baja.
	 */
	public void baja_usuario(String usuario) {
		enviar_mensaje("El usuario " + usuario + " se ha desconectado.", "Administrador");
	}
	
	/**
	 * Método para verificar si un usuario está baneado.0
	 * @param usuario1 Usuario que banea
	 * @param usuario2 Usuario a banear
	 * @return
	 */
	public boolean esta_baneado(String usuario1, String usuario2) {
		return baneos.get(usuario1).contains(usuario2);
	}
	
	/**
	 * Método para banear un usuario.
	 * @param usuario Usuario que quiere banear.
	 * @param usuario_a_banear Usuario a banear.
	 */
	public void banear(String usuario, String usuario_a_banear)
	{
		HashSet<String> baneados_usuario = baneos.get(usuario);
		List<String> lista_mensajes = mensajes.get(usuario);
		if(baneados_usuario.add(usuario_a_banear)) {
			lista_mensajes.add("El usuario " + usuario_a_banear + " ha sido baneado.");
			mensajes.put(usuario, lista_mensajes);
		} else {
			lista_mensajes.add("El usuario " + usuario_a_banear + " ya estaba baneado.");
			mensajes.put(usuario, lista_mensajes);
		}
	}
	
	/**
	 * Método para desbanear a un usuario
	 * @param usuario Usuario que desbanea
	 * @param usuario_a_desbanear Usuario a desbanear
	 */
	public void desbanear(String usuario, String usuario_a_banear)
	{
		HashSet<String> baneados_usuario = baneos.get(usuario);
		List<String> mensajes_usuario = mensajes.get(usuario);
		if(baneados_usuario.remove(usuario_a_banear)) {
			mensajes_usuario.add("El usuario " + usuario_a_banear + "ha sido desbaneado.");
			mensajes.put(usuario, mensajes_usuario);
		} else {
			mensajes_usuario.add("El usuario " + usuario_a_banear + "no estaba baneado.");
			mensajes.put(usuario, mensajes_usuario);
		}
	}
	
}