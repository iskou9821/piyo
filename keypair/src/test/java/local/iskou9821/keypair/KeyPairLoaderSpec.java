package local.iskou9821.keypair;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class KeyPairLoaderSpec {
	private static final String KEY_DIR="./target/keys";
	private static final String PUBLIC_KEY = KEY_DIR + "/test.key.pub";
	private static final String PRIVATE_KEY = KEY_DIR + "/test.key";

	@Before
	public void キーペアを作成() throws IOException {
		File f = new File(KEY_DIR);
		if (!f.exists()) f.mkdirs();
		KeyPair p =KeyPairCreator.createKeyPair();
		KeyPairCreator.saveKeyPair(p, PRIVATE_KEY, PUBLIC_KEY);
	}

	@After
	public void キーペアを削除() {
		File f1 = new File(PRIVATE_KEY);
		if (f1.exists()) f1.delete();

		File f2 = new File(PUBLIC_KEY);
		if (f2.exists()) f2.delete();
	}

	@Test
	public void 秘密鍵および公開鍵で暗号化する() throws Exception {
		KeyPair p = getKeyPair();

		String s = "hogehogehoge";

		//公開鍵で暗号化
		byte[] enc = KeyPairCrypter.encrypt(s.getBytes(), p.getPublic());
		byte[] dec = KeyPairCrypter.decrypt(enc, p.getPrivate());
		assertThat(enc, not(dec));
		assertThat(s, is(new String(dec)));

		//秘密鍵で暗号化
		enc = KeyPairCrypter.encrypt(s.getBytes(), p.getPrivate());
		dec = KeyPairCrypter.decrypt(enc, p.getPublic());
		assertThat(enc, not(dec));
		assertThat(s, is(new String(dec)));
	}

	private KeyPair getKeyPair() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
		return KeyPairLoader.load(PRIVATE_KEY, PUBLIC_KEY);
	}
}
