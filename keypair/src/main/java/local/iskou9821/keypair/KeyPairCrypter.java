package local.iskou9821.keypair;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class KeyPairCrypter {
	public static byte[] encrypt(byte[] data, Key key) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		Cipher c = getCipher();
		c.init(Cipher.ENCRYPT_MODE, key);
		return c.doFinal(data);
	}

	public static byte[] decrypt(byte[] data, Key key) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		Cipher c = getCipher();
		c.init(Cipher.DECRYPT_MODE, key);
		return c.doFinal(data);
	}

	private static Cipher getCipher() {
		try {
			return Cipher.getInstance("RSA/ECB/PKCS1PADDING"); //Cipherはスレッドセーフではないらしい。
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
