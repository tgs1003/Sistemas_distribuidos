package es.ubu.lsi.server;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClassLoader;
import java.util.Properties;

/**
 * Inicia el proceso de exportación del servidor remoto y su registro en
 * RMI.
 * 
 * @author Teodoro Ricardo García Sánchez
 * 
 *
 */
public class ChatServerStarter {

	public ChatServerStarter() {
		try {
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new SecurityManager());
			}
			ChatServer server = new ChatServerImpl();

			// registramos al servidor en el registro con el nombre de servidor
			// Registry registry = LocateRegistry.getRegistry();
			// registry.rebind("ChatServerImpl", server);
			// System.out.println("Clase servidor lista");

			Properties p = System.getProperties();
			// lee el codebase
			String url = p.getProperty("java.rmi.server.codebase");
			// cargador de clases
			Class<?> serverClass = RMIClassLoader.loadClass(url, "es.ubu.lsi.server.ChatServerImpl");
			// inicia el cliente
			Naming.rebind("/ChatServerImpl", (Remote) serverClass.getDeclaredConstructor().newInstance());
			System.out.println("Servidor activo...");
		} catch (Exception e) {
			System.err.println("Excepcion en arranque del servidor " + e.toString());
		}
	}
}
