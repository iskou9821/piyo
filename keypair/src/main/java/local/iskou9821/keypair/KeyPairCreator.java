package local.iskou9821.keypair;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class KeyPairCreator {

	public static KeyPair createKeyPair()  {
		KeyPairGenerator gen;
		try {
			gen = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException(e);
		}
		gen.initialize(2048);
		return gen.generateKeyPair();
	}

	public static void saveKeyPair(KeyPair keyPair, String privateKeyPath, String publicKeyPath) throws IOException {
		saveFile(keyPair.getPrivate().getEncoded(), privateKeyPath);
		saveFile(keyPair.getPublic().getEncoded(), publicKeyPath);
	}

	private static void saveFile(byte[] file, String path) throws IOException {
		try (FileOutputStream fout = new FileOutputStream(path)) {
			fout.write(file);
		}
	}
}
