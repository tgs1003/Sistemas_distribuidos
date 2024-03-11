/**
 * 
 */
package es.ubu.lsi.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import es.ubu.lsi.common.ChatMessage;
import es.ubu.lsi.common.ChatMessage.MessageType;

/**
 * Clase cliente del chat.
 * 
 * @author Teodoro Ricardo García Sánchez
 *
 */
public class ChatClientImpl implements ChatClient {

	/**
	 * Servidor al que conectarse.
	 */
	private String server;

	/**
	 * Nombre de usuario.
	 */
	private String username;

	/**
	 * Puerto de conexión.
	 */
	private int port;

	/**
	 * Condicionante para dejar de enviar.
	 */
	private static boolean carryOn = true;

	/**
	 * Id del usuario.
	 */
	private static int id;

	/**
	 * Canal de entrada.
	 */
	private ObjectInputStream in;

	/**
	 * Canal de salida.
	 */
	private ObjectOutputStream out;

	/**
	 * Construye un cliente.
	 * 
	 * @param server Dirección del servidor.
	 * @param port Puerto.
	 * @param username Nombre del usuario.
	 */
	public ChatClientImpl(String server, int port, String username) {
		this.server = server;
		this.port = port;
		this.username = username;
	}

	/**
	 * Inicializa el cliente.
	 * 
	 * @return Devuelve si el cliente ha arrancado.
	 */
	@SuppressWarnings("resource")
	public boolean start() {
		Socket clientSocket = null;

		try {
			// Creamos el socket
			clientSocket = new Socket(server, port);

			// Crear canales de entrada y salida
			this.out = new ObjectOutputStream(clientSocket.getOutputStream());
			this.in = new ObjectInputStream(clientSocket.getInputStream());

			// Ejecutar hilo
			new Thread(new ChatClientListener()).start();

			// Indicamos el nombre de usuario
			out.writeObject(username);

			// Informamos de la conexion
			// System.out.println("Conexión satisfactoria");
		} catch (UnknownHostException e) {
			System.err.println("No se puede conectar con el servidor: " + server);
			return false;
		} catch (IOException e) {
			System.err.println("No se puede establecer conexión entrada/salida con:" + server);
			return false;
		}
		return true;
	}

	/**
	 * Enviar mensaje.
	 * 
	 * @param msg Mensaje a enviar.
	 */
	public void sendMessage(ChatMessage msg) {
		try {
			this.out.reset();
			this.out.writeObject(msg);
		} catch (IOException e) {
			System.out.println("No se puede enviar el mensaje");
		}
	}

	/**
	 * Desconecta el cliente.
	 */
	public void disconnect() {
		try {
			if (this.in != null) {
				this.in.close();
			}
			if (this.out != null) {
				this.out.close();
			}
		} catch (IOException e) {
			System.err.println("Error al desconectar cliente '" + this.username + "'.");
		}
	}

	/**
	 * Arranca el hilo principal de ejecución del cliente.
	 * 
	 * @param args argumentos
	 */
	public static void main(String[] args) {
		// Variables para la conexión
		String hostName = "localhost";
		int portNumber = 1500;
		String username = null;
		Scanner scan = new Scanner(System.in);

		// Argumentos de entrada
		if (args.equals(null) || args.length != 2) {
			System.out.println("Introduce un nombre de usuario: ");
			username = scan.nextLine();

		} else if (args.length == 1) {
			username = args[0];

		} else if (args.length == 2) {
			hostName = args[0];
			username = args[1];
		}

		// Listener de mensajes para el cliente
		ChatClientImpl cliente = new ChatClientImpl(hostName, portNumber, username);

		if (cliente.start()) {
			System.out.println("Has entrado correctamente en el chat\n");
			System.out.println("Tu usuario es: " + username);
			System.out.println("Los comandos disponibles son: \n");
			System.out.println("    - LOGOUT ");
			System.out.println("    - SHUTDOWN ");
			System.out.println("    - BAN <Usuario>");
			System.out.println("    - UNBAN <Usuario>");
			System.out.println("________________________________________\n");

			Scanner scan2 = new Scanner(System.in);

			while (carryOn) {
				System.out.print("> ");
				String message = scan2.nextLine();
				if (message.equalsIgnoreCase("LOGOUT")) {
					cliente.sendMessage(new ChatMessage(id, ChatMessage.MessageType.LOGOUT, ""));
					carryOn = false;
				} else if (message.equalsIgnoreCase("SHUTDOWN")) {
					cliente.sendMessage(new ChatMessage(id, ChatMessage.MessageType.SHUTDOWN, ""));	
				}
				else {
					cliente.sendMessage(new ChatMessage(id, ChatMessage.MessageType.MESSAGE, message));
				}
			}
			System.out.println("Desconectando. Pulsa la tecla intro para cerrar...");
			scan.nextLine();
			scan.close();
			scan2.close();
		} else {
			System.err.println("No se puede arrancar el cliente.");
		}
		cliente.disconnect();
		System.exit(1);
	}

	/**
	 * Hilo de escucha de mensajes en el servidor.
	 * 
	 * @author Teodoro Ricardo García Sánchez
	 *
	 */
	private class ChatClientListener implements Runnable {

		/**
		 * Ejecución del hilo.
		 */
		public void run() {
			String message;

			try {
				// Aqui establecemos el nuevo id del mensaje que nos ha devuelto
				// el hilo del server.
				id = ((ChatMessage) in.readObject()).getId();
				while ((message = (String) in.readObject()) != null) {
					// Imprimimos lo que recibimos del servidor
					System.out.println(message);
					System.out.println("> ");
				}
			} catch (ClassNotFoundException e) {
				System.err.println("Error al recibir mensaje.");
			} catch (IOException e) {
				System.err.println("Te has desconectado del servidor " + server + ".");
				carryOn = false;
			}
		}
	}
}
