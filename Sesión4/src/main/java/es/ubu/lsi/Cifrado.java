package es.ubu.lsi;

import java.rmi.Remote;

import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Interfaz de cifrado
 * 
 * @author Teodoro Ricardo García Sánchez
 */
public interface Cifrado extends Remote
{
	
	String cifrar(String clave, String texto) throws RemoteException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException;
}
