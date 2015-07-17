package local.iskou9821.keypair;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class PasswordCrypterSpec {
	private static final byte[] salt = {10, 20, 15, 18, 50, 45, 46, 47};
	private static final int iteration = 10;

	@Test
	public void パスワードで暗号化と復号化() throws UnsupportedEncodingException {
		String s = "hogehoge";
		char[] pwd = {'p', 'i', 'y', 'o', 'p', 'i', 'y', 'o'};

		byte[] enc = PasswordCrypter.encrypt(s.getBytes("UTF-8"), salt, iteration, pwd);
		byte[] dec = PasswordCrypter.decrypt(enc, salt, iteration, pwd);
		assertThat(enc, not(dec));
		assertThat(s, is(new String(dec, "UTF-8")));
	}

}
