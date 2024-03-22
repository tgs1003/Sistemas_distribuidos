package es.ubu.lsi;
	
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Clase que cifra un texto con la contraseña especificada.
 *
 * @author Teodoro Ricardo García Sánchez
 */	
public class CifradoImpl implements Cifrado {
	

	@Override
	public String cifrar(String clave, String texto) throws RemoteException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		// Create key and cipher
        Key aesKey = new SecretKeySpec(clave.getBytes(), "AES");	
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] bytes = cipher.doFinal(texto.getBytes());
        return bytes.toString();
	}
	
}