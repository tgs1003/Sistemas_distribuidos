package es.ubu.lsi.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import es.ubu.lsi.client.ChatClient;
import es.ubu.lsi.common.ChatMessage;

/**
 * Realiza la carga dinámica de clases desde el servidor.
 * 
 * @author Teodoro Ricardo García Sánchez
 * 
 *
 */
public class ChatServerImpl extends UnicastRemoteObject implements ChatServer {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Numero de clientes en el chat.
	 */
	private static int clientesTotales = 0;

	/**
	 * Nombre del servidor.
	 */
	private static String SERVER_NAME = "SERVER";

	/**
	 * Lista de clientes.
	 */
	private ArrayList<ChatClient> listaClientes;

	/**
	 * Mapa para llevar la cuenta de los baneos;
	 */
	private HashMap<String, HashSet<String>> listaBaneos;

	/**
	 * Contruye un servidor
	 * 
	 * @throws RemoteException
	 *             RemoteException
	 */
	public ChatServerImpl() throws RemoteException {
		super();
		listaClientes = new ArrayList<ChatClient>();
		listaBaneos = new HashMap<String, HashSet<String>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ubu.lsi.server.ChatServer#checkIn(es.ubu.lsi.client.ChatClient)
	 */
	public int checkIn(ChatClient client) throws RemoteException {
		int id = -1;
		if (!checkClientExist(client.getNickName())) {
			id = clientesTotales++;
			client.setId(id);
			listaClientes.add(client);
			
			publish(new ChatMessage(-1, SERVER_NAME, client.getNickName() + " se ha conectado."));
		}
		return id;
	}

	/**
	 * Comprueba si el nombre de usuario ya está escogido.
	 * 
	 * @param nickName
	 *            nombre de usuario a validar
	 * @return true/false si existe o no el nombre
	 */
	private boolean checkClientExist(String nickName) {
		boolean existe = false;
		for (ChatClient client : listaClientes) {
			try {
				if (client.getNickName().toLowerCase().equals(nickName.toLowerCase())) {
					existe = true;
					break;
				}
			} catch (RemoteException e) {
				System.err.println("No se puede acceder a la lista de usuarios activos.");
			}
		}
		return existe;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ubu.lsi.server.ChatServer#logout(es.ubu.lsi.client.ChatClient)
	 */
	public void logout(ChatClient client) throws RemoteException {
		listaClientes.remove(client);
		publish(new ChatMessage(-1, SERVER_NAME, client.getNickName() + " se ha desconectado"));
		System.out.println(client.getNickName() + " se ha desconectado");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ubu.lsi.server.ChatServer#privatemsg(java.lang.String,
	 * es.ubu.lsi.common.ChatMessage)
	 */
	public void privatemsg(String tonickname, ChatMessage msg) throws RemoteException {
		ChatClient emisor = null;
		for (ChatClient client : listaClientes) {
			if (msg.getId() == client.getId()) {
				emisor = client;
				break;
			}
		}

		for (ChatClient receptor : listaClientes) {
			if (receptor.getNickName().toLowerCase().equals(tonickname)) {
				receptor.receive(new ChatMessage(msg.getId(), emisor.getNickName(), msg.getMessage() + " (private)"));
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ubu.lsi.server.ChatServer#publish(es.ubu.lsi.common.ChatMessage)
	 */
	public void publish(ChatMessage msg) throws RemoteException {
		// Gestionar el mensaje
		for (ChatClient client : listaClientes) {
			// Si el id es distinto lo mandamos
			
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ubu.lsi.server.ChatServer#shutdown(es.ubu.lsi.client.ChatClient)
	 */
	public void shutdown(ChatClient client) throws RemoteException {
		for (ChatClient cliente : listaClientes) {
			if (cliente.getId() != client.getId()) {
				cliente.receive(new ChatMessage(-1, SERVER_NAME, "El servidor se cerrará."));
			}
		}
		listaClientes = null;
		System.exit(0);
	}

	/**
	 * Elimina a un usuario.
	 * 
	 * @param client_id
	 *            	cliente que manda el mensaje
	 * @param client_to_drop
	 * 				cliente a eliminar
	 * @throws RemoteException remote error
	 */
	public void drop(int client_id, String client_to_drop) throws RemoteException {
		ChatClient usuario_a_borrar = null;
		for (ChatClient cliente : listaClientes) {
			if (cliente.getNickName() == client_to_drop) {
				usuario_a_borrar = cliente;
				break;	
			}
		}
		ChatClient usuario_a_notificar = null;
		for (ChatClient cliente : listaClientes) {
			try {
				if (cliente.getId() == client_id) {
					usuario_a_notificar = cliente;
					break;
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(usuario_a_borrar != null) {
			listaClientes.remove(usuario_a_borrar);
			usuario_a_notificar.receive(new ChatMessage(-1, SERVER_NAME, "El usuario " + client_to_drop + " ha sido desconectado."));
		}
		else
		{
			usuario_a_notificar.receive(new ChatMessage(-1, SERVER_NAME, "El usuario " + client_to_drop + " no existe."));
		}
	}

}
