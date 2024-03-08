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
	 * Construye un servidor.
	 * 
	 * @param port
	 *            puerto para establecer conexion
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
	 * Elimina a un usuario del chat
	 */
	private void drop(int emisor, String usuario_a_eliminar) {
		String msg = "El usuario " + usuario_a_eliminar + " no existe";
		if(remove(usuario_a_eliminar))
		{
			msg = "El usuario " + usuario_a_eliminar + "ha sido desconectado";
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
				if (client.getClientId() != message.getId()) {
					client.out.writeObject(msg);
				}
			} catch (IOException e) {
				System.out.println("# " + client.getUsername() + "No recibe mensajes.");
			}
		}
	}
	
	/**
	 * Elimina a un cliente de la lista
	 * 
	 * @param userName El nombre del usuario a eliminar
	 * 
	 * @return Devuelve true si el usuario se ha podido desconectar.
	 */
	private boolean remove(String userName)
	{
		ServerThreadForClient cliente = null;
		boolean resultado = false;
		for (ServerThreadForClient client : usersList) {
				System.out.println(client.getName());
				if (client.getName() == userName) {
					usersList.remove(cliente);
					resultado = true;
					break;
				}
		}
		return resultado;
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
						broadcast(message, getUsername());
					break;
				case DROP:
						drop(getClientId(), message.getMessage());
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
