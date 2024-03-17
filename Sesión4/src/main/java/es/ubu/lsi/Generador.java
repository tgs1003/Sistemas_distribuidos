package es.ubu.lsi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface remoto de para la clase Generador
 * 
 * @author Teodoro Ricardo García Sánchez
 */
public interface Generador  extends Remote{

	int generar(int rango) throws RemoteException;
}
