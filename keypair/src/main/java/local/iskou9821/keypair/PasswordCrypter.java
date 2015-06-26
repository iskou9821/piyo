package local.iskou9821.keypair;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class PasswordCrypter {
	private static final String ALGORITHM="PBEWithMD5AndDES";

	//Stringだとメモリ上に残りやすいので、パスワードはchar[]で受け取った方が良いらしい。
	public static byte[] encrypt(byte[] src, byte[] salt, int iteration, char[] password) {
		try {
			return getCipher(salt, iteration, password, Cipher.ENCRYPT_MODE).doFinal(src);
		} catch (IllegalBlockSizeException | InvalidKeySpecException
							| NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
							| InvalidAlgorithmParameterException | BadPaddingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static byte[] decrypt(byte[] src, byte[] salt, int iteration, char[] password) {
		try {
			return getCipher(salt, iteration, password, Cipher.DECRYPT_MODE).doFinal(src);
		} catch (IllegalBlockSizeException | InvalidKeySpecException
				| NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException | BadPaddingException e) {
			throw new IllegalArgumentException(e);
		}
	}


	private static Cipher getCipher(byte[] salt, int iteration, char[] password, int mode)
										throws InvalidAlgorithmParameterException, InvalidKeyException,
											NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		PBEKeySpec keySpec = new PBEKeySpec(password);
		SecretKeyFactory fac = SecretKeyFactory.getInstance(ALGORITHM);
		SecretKey key = fac.generateSecret(keySpec);
		PBEParameterSpec paramSpec = new PBEParameterSpec(salt, iteration);
		cipher.init(mode, key, paramSpec);
		return cipher;
	}
}
