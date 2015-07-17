package local.test.imap4;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.Properties;

public class MailBoxChecker {
	public interface MailBoxCheckCallback {
		public void execute(Message message) throws MessagingException, IOException;
	}

	public void execute(MailBoxConnectParams params,
							String targetFolder, MailBoxCheckCallback callback) throws MessagingException, IOException {
		Properties props = System.getProperties();
		Session sess = Session.getInstance(props, null);

		Store st = sess.getStore("imaps"); //with SSL
		try {
			st.connect(params.getHost(), params.getPort(), params.getUser(), params.getPassword());
			Folder fol = st.getFolder(targetFolder);
			if(fol.exists()){
				for(Folder f : fol.list()){
					System.out.println(f.getName());

				}
				fol.open(Folder.READ_WRITE); //書き込み可能で開かないと既読を付けられない
				FlagTerm unread = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
				for(Message m : fol.search(unread)){
					callback.execute(m);
				}
				fol.close(false);
			}else{
				System.out.printf("%s is not exist.", targetFolder);
			}
		} finally {
			st.close();
		}
	}

}
