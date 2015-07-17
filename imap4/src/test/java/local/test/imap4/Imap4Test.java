package local.test.imap4;

import org.junit.Test;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;

public class Imap4Test {

	@Test
	public void メールボックスを開く() {
		MailBoxChecker checker = new MailBoxChecker();
		try {
			checker.execute(getParams(), "INBOX", new MailBoxChecker.MailBoxCheckCallback() {
				@Override
				public void execute(Message message) throws MessagingException, IOException {
					System.out.printf("題名:%s", message.getSubject());

					String s = (String)message.getContent();
					System.out.printf("本文:%s", s);
					message.setFlag(Flags.Flag.SEEN, true);
				}
			});
		} catch (MessagingException |IOException e) {
			e.printStackTrace();
		}
	}

	public static MailBoxConnectParams getParams() {
		return new MailBoxConnectParams(
				"[ホスト名]",
				993, //ポート番号
				"ユーザー名",
				"パスワード");
	}
}
