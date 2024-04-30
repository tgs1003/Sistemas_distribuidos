package es.ubu.lsi.server;

import java.rmi.Naming;
import java.rmi.Remote;
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
