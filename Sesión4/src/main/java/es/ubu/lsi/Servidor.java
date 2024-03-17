package es.ubu.lsi;
	
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Servidor remoto.
 * 
 * @author Teodoro Ricardo García Sánchez
 */	
public class Servidor implements HolaMundo {
	
	/**
	 * {@inheritDoc}.
	 *
	 * @return {@inheritDoc}
	 */
    public String decirHola() {
		return "Hola mundo!";
    }
	
	/**
	 * Método raíz.
	 *
	 * @param args argumentos
	 */
    public static void main(String args[]) {
	
		try {
		    Servidor obj = new Servidor();
		    
		    // si no hereda de UnicastRemoteObject es necesario exportar
	    	HolaMundo stub = (HolaMundo) UnicastRemoteObject.exportObject(obj, 0);

		    // Liga el resguardo de objeto remoto en el registro
	    	Registry registro = LocateRegistry.getRegistry();
	    	registro.bind("Hola", stub);
	    	//Registramos un objeto de la clase Generador
	    	Generador obj_generador = new GeneradorImpl();
	    	Generador stub_aleatorio = (Generador) UnicastRemoteObject.exportObject(obj_generador, 0);
	    	registro.bind("Aleatorio", stub_aleatorio);
	    	//Regisstramos un objeto de las clase Cifrador
	    	CifradoImpl obj_cifrador = new CifradoImpl();
	    	Cifrado stub_cifrado = (Cifrado) UnicastRemoteObject.exportObject(obj_cifrador, 0);
	    	registro.bind("Cifrado", stub_cifrado);
	    	
	    	System.out.println("Servidor preparado");
		}
		catch (Exception e) {
		    System.err.println("Excepción de servidor: " + e.toString());
		}
    } // main
    
} // Servidor