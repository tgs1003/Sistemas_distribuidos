/**
 * 
 */
package es.ubu.lsi.client;

import es.ubu.lsi.common.ChatMessage;

/**
 * Interfaz para el cliente del chat.
 * 
 * @author Teodoro Ricardo García Sánchez
 *
 */
public interface ChatClient {

	/**
	 * Inicializa el cliente
	 * 
	 * @return Devuelve si el cliente ha arrancado.
	 */
	public boolean start();

	/**
	 * Método que envía un mensaje.
	 * 
	 * @param msg El texto a enviar.
	 *            
	 */
	public void sendMessage(ChatMessage msg);

	/**
	 * Desconecta el cliente.
	 */
	public void disconnect();

}
