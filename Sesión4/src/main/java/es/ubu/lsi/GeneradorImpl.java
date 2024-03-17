package es.ubu.lsi;
	
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

/**
 * Clase para generar números aleatorios
 *
 * @author Teodoro Ricardo García Sánchez
 */	
public class GeneradorImpl implements Generador {
	
	@Override
	public int generar(int rango) throws RemoteException {
		Random aleatorio = new Random(System.currentTimeMillis());
		return aleatorio.nextInt(rango);
	}
    
} // Servidor