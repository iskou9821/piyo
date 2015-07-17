package local.iskou9821.keypair;

import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyPairLoader {

	public static KeyPair loadWithPassword(String privateKeyPath, String publicKeyPath,
										   char[] privKeyPassword, byte[] salt, int iteration)
												throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] pub = loadFile(publicKeyPath);
		byte[] priv = loadFile(privateKeyPath);
		byte[] priv_dec = PasswordCrypter.decrypt(priv, salt, iteration, privKeyPassword);
		return _load(pub, priv_dec);
	}

	public static KeyPair load(String privateKeyPath, String publicKeyPath)
						throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] pub = loadFile(publicKeyPath);
		byte[] priv = loadFile(privateKeyPath);

		return _load(pub, priv);
	}

	private static KeyPair _load(byte[] pub, byte[] priv) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory fac = KeyFactory.getInstance("RSA");

		X509EncodedKeySpec pubKeySpec =new X509EncodedKeySpec(pub);
		PublicKey pubKey = fac.generatePublic(pubKeySpec);

		PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(priv);
		PrivateKey privKey = fac.generatePrivate(privKeySpec);

		return new KeyPair(pubKey, privKey);
	}

	private static byte[] loadFile(String path) throws IOException {
		File f = new File(path);

		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				FileInputStream fin = new FileInputStream(f)) {
			int i;
			while ((i = fin.read()) >= 0) {
				out.write(i);
			}
			return out.toByteArray();
		}
	}
}
