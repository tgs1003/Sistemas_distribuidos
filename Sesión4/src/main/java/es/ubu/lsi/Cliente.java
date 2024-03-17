package es.ubu.lsi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Cliente remoto.
 * 
 * @author Teodoro Ricardo García Sánchez
 */
public class Cliente {

	/**
	 * Constructor oculto,
	 */
    private Cliente() {}


	/**
	 * Método raíz.
	 *
	 * @param args host con el registro
	 */
    public static void main(String[] args) {

		String host = (args.length < 1) ? null : args[0];
		try {
		   Registry registry = LocateRegistry.getRegistry(host);
		   // Resuelve el objeto remoto (la referencia a...)
	 	   HolaMundo stub = (HolaMundo) registry.lookup("Hola");
	 	   String respuesta = stub.decirHola();
	       System.out.println("Respuesta del servidor remoto: " + respuesta);
	       Generador stub_aleatorio = (Generador) registry.lookup("Aleatorio");
	 	   Integer respuesta_aleatorio = stub_aleatorio.generar(100);
	       System.out.println("Respuesta del servidor remoto (número aleatorio): " + respuesta_aleatorio);
	       Cifrado stub_cifrado = (Cifrado) registry.lookup("Cifrado");
	 	   String respuesta_cifrado = stub_cifrado.cifrar("bar12345bar12345","Texto cifrado");
	       System.out.println("Respuesta del servidor remoto (cifrado): " + respuesta_cifrado);
		} 
		catch (Exception e) {
	    	System.err.println("Excepción en cliente: " + e.toString());
		} // try
		
    } // main
    
} // Cliente