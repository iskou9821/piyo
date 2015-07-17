package local.iskou9821.keypair;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class KeyPairLoaderSpec {
	private static final byte[] ENC_SALT = {13, 24, 35, 46, 57, 68, 79, 80};
	private static final int ENC_ITERATION = 10;
	private static final char[] ENC_PWD = {'p', 'i', 'y', 'o', 'p', 'i', 'y', 'o'};

	private static final String KEY_DIR="./target/keys";
	private static final String PUBLIC_KEY = KEY_DIR + "/test.key.pub";
	private static final String PRIVATE_KEY = KEY_DIR + "/test.key";
	private static final String PUBLIC_KEY_ENCMODE = KEY_DIR + "/test.key.pub.encmode";
	private static final String PRIVATE_KEY_ENCMODE = KEY_DIR + "/test.key.encmode";

	@Before
	public void キーペアを作成() throws IOException {
		File f = new File(KEY_DIR);
		if (!f.exists()) f.mkdirs();

		//パスワードなしの秘密鍵
		KeyPair p =KeyPairCreator.createKeyPair();
		KeyPairCreator.saveKeyPair(p, PRIVATE_KEY, PUBLIC_KEY);

		//パスワードありの秘密鍵
		KeyPair p2 = KeyPairCreator.createKeyPair();
		KeyPairCreator.saveKeyPairWithPassword(
				p2, PRIVATE_KEY_ENCMODE, PUBLIC_KEY_ENCMODE, ENC_PWD, ENC_SALT, ENC_ITERATION);
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

		encryptAndDecrypt(p, s);
	}

	@Test
	public void パスワード保護した秘密鍵および公開鍵で暗号化する() throws Exception {
		KeyPair p = getKeyPairEncMode();
		String s = "テストです　テストです　テストです　テストです　テストです";

		encryptAndDecrypt(p, s);
	}

	private void encryptAndDecrypt(KeyPair p, String s)
			throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException, UnsupportedEncodingException {
		byte[] enc = KeyPairCrypter.encrypt(s.getBytes("UTF-8"), p.getPublic());
		byte[] dec = KeyPairCrypter.decrypt(enc, p.getPrivate());
		assertThat(enc, not(dec));
		assertThat(s, is(new String(dec, "UTF-8")));

		//秘密鍵で暗号化
		enc = KeyPairCrypter.encrypt(s.getBytes(), p.getPrivate());
		dec = KeyPairCrypter.decrypt(enc, p.getPublic());
		assertThat(enc, not(dec));
		assertThat(s, is(new String(dec)));
	}

	private KeyPair getKeyPair() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
		return KeyPairLoader.load(PRIVATE_KEY, PUBLIC_KEY);
	}

	private KeyPair getKeyPairEncMode() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
		return KeyPairLoader.loadWithPassword(
						PRIVATE_KEY_ENCMODE, PUBLIC_KEY_ENCMODE, ENC_PWD, ENC_SALT, ENC_ITERATION);
	}

}
