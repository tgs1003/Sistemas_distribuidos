/**
 * 
 */
package es.ubu.lsi.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.ubu.lsi.common.ChatMessage;
import es.ubu.lsi.common.ChatMessage.MessageType;

/**
 * Clase para el chat del servidor.
 * 
 * @author Teodoro Ricardo García Sánchez
 *
 */
public class ChatServerImpl implements ChatServer {

	/**
	 * Puerto por defecto.
	 */
	private static final int DEFAULT_PORT = 1500;

	/**
	 * Id del cliente.
	 */
	private static int clientId = 0;

	/**
	 * Fecha.
	 */
	private SimpleDateFormat sdf;

	/**
	 * Puerto.
	 */
	private int port;

	/**
	 * Si el hilo esta vivo o no.
	 */
	private boolean alive;

	/**
	 * Lista de usuarios conectados.
	 */
	private ArrayList<ServerThreadForClient> usersList;
	
	/**
	 * Lista de usuarios conectados.
	 */
	private Map<Integer, ArrayList<String>> bannedUsers;

	/**
	 * Construye un servidor.
	 * 
	 * @param port puerto para establecer conexion
	 */
	public ChatServerImpl(int port) {
		// Validar si el puerto esta disponible y sino poner por defecto
		try {
			Socket test = new Socket("localhost", port);
			this.port = port;
			test.close();
		} catch (Exception e) {
			this.port = DEFAULT_PORT;
		}
	}

	/**
	 * Espera y acepta peticiones de los clientes.
	 */
	public void startup() {
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		try {
			serverSocket = new ServerSocket(port);
			alive = true;
			usersList = new ArrayList<ServerThreadForClient>();
			bannedUsers = new HashMap<Integer, ArrayList<String>>();
			sdf = new SimpleDateFormat("HH:mm:ss");
			System.out.println("Servidor iniciado...");
			System.out.println("________________________________________\n");
		} catch (IOException e) {
			System.err.println("No se puede iniciar el servidor.");
		}

		while (alive) {
			try {
				clientSocket = serverSocket.accept();
				ServerThreadForClient clientThread = new ServerThreadForClient(clientSocket, clientId++);
				usersList.add(clientThread);
				clientThread.start();
			} catch (IOException e) {
				System.err.println("# Cliente no aceptado.");
			}
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.err.println("No se puede cerrar el servidor.");
		} finally {
			// Apagar el servidor
			System.exit(1);
		}
	}

	/**
	 * Apaga correctamente el servidor, finalizando los clientes que haya.
	 */
	public void shutdown() {
		if (usersList.size() != 0) {
			for (ServerThreadForClient client : usersList) {
				client.finalizarCliente();
			}
		}
		System.out.println("Apagando servidor...");
		alive = false;
	}
	
	/**
	 * Banea a un usuario del chat
	 */
	private void ban(int emisor, String usuario_a_banear) {
		String msg = "El usuario " + usuario_a_banear + " ya está baneado.";
		boolean baneado = false;
		if(bannedUsers.containsKey(emisor))
		{
			ArrayList<String> banned = bannedUsers.get(emisor);
			for (String user : banned)
			{
				if (user.equals(usuario_a_banear))
					baneado = true;
					break;
			}
			if(!baneado)
			{
				banned.add(usuario_a_banear);
				msg = "El usuario " + usuario_a_banear + "ha sido baneado.";
			}
		}
		else
		{
			ArrayList<String> banned = new ArrayList<String>();
			banned.add(usuario_a_banear);
			bannedUsers.put(emisor, banned);
			msg = "El usuario " + usuario_a_banear + " ha sido baneado.";
		}
		
		sendMessage(emisor, new ChatMessage(clientId, MessageType.MESSAGE, msg));
	}
	
	/**
	 * Desbanea a un usuario del chat
	 */
	private void unban(int emisor, String usuario_a_desbanear) {
		 
		String msg = "El usuario " + usuario_a_desbanear + " no está baneado.";
		boolean baneado = false;
		if(bannedUsers.containsKey(emisor))
		{
			System.out.println("Existe emisor: " + emisor);
			ArrayList<String> banned = bannedUsers.get(emisor);
			for (String user : banned)
			{
				if (user.equals(usuario_a_desbanear))
					baneado = true;
					break;
			}
			if(baneado)
			{
				banned.remove(usuario_a_desbanear);
				msg = "El usuario " + usuario_a_desbanear + " ha sido desbaneado.";
			}
		}
		
		sendMessage(emisor, new ChatMessage(clientId, MessageType.MESSAGE, msg));
		
	}
	
	/**
	 * Manda un mensaje a un cliente.
	 * 
	 * @param message mensaje a mandar
	 * 
	 * @param emisor Nombre del usuario que envia el mensaje
	 */
	public synchronized void sendMessage(int user, ChatMessage message) {
		String fecha = sdf.format(new Date());
		String msg = fecha + " " + message.getMessage();

		System.out.println(msg);

		for (ServerThreadForClient client : usersList) {
			try {
				if (client.getClientId() == user) {
					client.out.writeObject(msg);
				}
			} catch (IOException e) {
				System.out.println("# " + client.getUsername() + "No recibe mensajes.");
			}
		}
	}
	
	
	/**
	 * Manda un mensaje a todos los clientes.
	 * 
	 * @param message
	 *            mensaje a mandar
	 */
	public synchronized void broadcast(ChatMessage message) {
		String fecha = sdf.format(new Date());
		String msg = fecha + " " + message.getMessage();

		System.out.println(msg);

		for (ServerThreadForClient client : usersList) {
			try {
				if (client.getClientId() != message.getId()) {
					client.out.writeObject(msg);
				}
			} catch (IOException e) {
				System.out.println("# " + client.getUsername() + "No recibe mensajes.");
			}
		}
	}

	/**
	 * Manda un mensaje a todos los clientes.
	 * 
	 * @param message mensaje a mandar
	 * 
	 * @param emisor Nombre del usuario que envia el mensaje
	 */
	public synchronized void broadcast(ChatMessage message, String emisor) {
		String fecha = sdf.format(new Date());
		String msg = fecha + " " + emisor + ": " + message.getMessage();

		System.out.println(msg);
		
		for (ServerThreadForClient client : usersList) {
			try {
				Boolean baneado = false;
				for (Integer key : bannedUsers.keySet())
				{
					System.out.println("BannedKey: " + key);
					System.out.println("ClientId: " + client.getClientId());
				}
				if(bannedUsers.containsKey(client.getClientId()))
				{
					System.out.println("Contains key");
					ArrayList<String> baneados = bannedUsers.get(client.getClientId());
				
					for (String usuario : baneados) {
						System.out.println(usuario);
						if(usuario.equals(emisor)) {
							baneado=true;
						break;
						}
					}
				}
				if (client.getClientId() != message.getId() && !baneado) {
					client.out.writeObject(msg);
				}
			} catch (IOException e) {
				System.out.println("# " + client.getUsername() + "No recibe mensajes.");
			}
		}
	}
	
	
	/**
	 * Elimina un cliente de la lista.
	 * 
	 * @param id Id del cliente que vamos a eliminar
	 */
	public void remove(int id) {
		ServerThreadForClient cliente = null;
		String mensaje = "El usuario no está conectado.";
		for (ServerThreadForClient client : usersList) {
				if (client.getClientId() == id) {
					usersList.remove(cliente);
					mensaje = "El usuario no está conectado.";
					break;
				}
		}
		System.out.println(mensaje);
	}

	/**
	 * Arranca el hilo principal de ejecución del servidor
	 * 
	 * @param args argumentos pasados
	 */
	public static void main(String[] args) {
		ChatServerImpl server = new ChatServerImpl(DEFAULT_PORT);
		server.startup();
	}

	/**
	 * Hilo para comunicarse con el servidor.
	 * 
	 * @author Teodoro Ricardo García Sánchez
	 *
	 */
	private class ServerThreadForClient extends Thread {
		/**
		 * Id del cliente.
		 */
		private int id;

		/**
		 * Nombre de usuario.
		 */
		private String username;

		/**
		 * Canal de entrada.
		 */
		private ObjectInputStream in;

		/**
		 * Canal de salida.
		 */
		private ObjectOutputStream out;

		/**
		 * Socket del cliente.
		 */
		private Socket clientSocket;

		/**
		 * Booleano para ver si ha acabado el cliente.
		 */
		private boolean finalizado;

		public ServerThreadForClient(Socket clientSocket, int id) {

			this.clientSocket = clientSocket;
			this.id = id;
			
			try {
				this.out = new ObjectOutputStream(clientSocket.getOutputStream());
				this.in = new ObjectInputStream(clientSocket.getInputStream());
				this.username = (String) in.readObject();

			} catch (ClassNotFoundException e) {
				System.err.println("No se puede recuperar el nombre de usuario.");

			} catch (IOException e) {
				System.err.println("No se puede establecer comunicación cliente-servidor");
			}

			finalizado = false;
		}

		/**
		 * Inicializa los canales de entrada y salida para la comunicación del
		 * cliente y el servidor.
		 */
		@Override
		public void run() {
			broadcast(new ChatMessage(getClientId(), MessageType.MESSAGE, getUsername() + " se conectó."));

			// Esto es donde le pasamos al cliente, su nuevo id.
			try {
				out.writeObject(new ChatMessage(id, ChatMessage.MessageType.MESSAGE, username));
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			// Acciones a realizar mientras el hilo está escuchando
			while (!finalizado) {
				ChatMessage message = null;
				try {
					message = (ChatMessage) in.readObject();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					System.err.println("No se puede recuperar el mensaje");
				}
				switch (message.getType()) {
				case LOGOUT:
					setFinalizado(true);
					remove(getClientId());
					break;
				case MESSAGE:
					String message_text = message.getMessage();
					if(message_text.startsWith("BAN "))
					{
						ban(message.getId(), message_text.split("BAN ")[1]);
					}
					else if (message_text.startsWith("UNBAN "))
					{
						unban(message.getId(), message_text.split("UNBAN ")[1]);
					}
					else
					{
						broadcast(message, getUsername());
					}
					break;
				case SHUTDOWN:
					shutdown();
				}
			}
			finalizarCliente();
		}

		/**
		 * Cambia el valor para el bucle del hilo del cliente.
		 * 
		 * @param finalizado true/false
		 */
		public void setFinalizado(boolean finalizado) {
			this.finalizado = finalizado;
		}

		/**
		 * Obtiene el id del cliente.
		 * 
		 * @return Id del cliente
		 */
		public int getClientId() {
			return this.id;
		}

		/**
		 * Devuelve el nombre de usuario.
		 * 
		 * @return username
		 */
		public String getUsername() {
			return this.username;
		}

		/**
		 * Finaliza el hilo del cliente.
		 */
		public void finalizarCliente() {
			try {
				// Cerramos canales de entrada y salida
				this.out.close();
				this.in.close();
				clientSocket.close();
			} catch (IOException e) {
				System.err.println("No se puede eliminar conexión con el cliente.");
			} finally {
				System.out.println("# Desconectando cliente: " + getUsername() + ".");
				// Cerramos el hilo
				this.interrupt();
			}
		}
	}
}
