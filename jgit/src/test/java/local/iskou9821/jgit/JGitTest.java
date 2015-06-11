package local.iskou9821.jgit;

import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Date;
import java.util.Properties;

public class JGitTest {
	private String uri = null;
	private String dir = null;
	private String user = null;
	private String pass = null;

	@Before
	public void setup() throws IOException {
		Properties p = new Properties();
		try (InputStream input = new FileInputStream("/var/conf/repository.properties")) {
			p.load(input);
			uri = p.getProperty("repo");
			dir = p.getProperty("dir");
			user = p.getProperty("user");
			pass = p.getProperty("password");
		}
	}

	@Test
	public void cloneする() throws GitAPIException {
		Git.cloneRepository().setURI(uri).setDirectory(new File(dir)).setCredentialsProvider(cred()).call();
	}

	@Test
	public void fetchする() throws IOException, GitAPIException {
		Git.open(new File(dir)).fetch().setCredentialsProvider(cred()).call();
	}

	@Test
	public void fetch_ブランチがあれば削除_リモートブランチをチェックアウト() throws IOException, GitAPIException {
		Git git = Git.open(new File(dir));

		//フェッチしてから一旦リセット
		git.fetch().setCredentialsProvider(cred()).call();
		git.reset().setMode(ResetCommand.ResetType.HARD).call();

		String branchName = "test";
		String prefix = "refs/heads";
		String fullName = prefix + "/" + branchName;

		//ブランチが存在するかどうか確認する
		boolean branchExists = false;
		for (Ref ref : git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call()) {
			System.out.println(ref.getName());
			if (fullName.equals(ref.getName())) {
				branchExists = true; break;
			}
		}

		//ブランチが存在していれば一旦削除
		if (branchExists) {
			if (git.getRepository().getBranch().equals(branchName)) {
				git.checkout().setName("origin/master").setForce(true).call();
			}

			git.branchDelete().setBranchNames(branchName).setForce(true).call();
		}

		//リモートからブランチを新規作成
		git.branchCreate()
				.setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)
				.setStartPoint("origin/" + branchName)
				.setName(branchName)
				.call();

		//作成したブランチをチェックアウト
		git.checkout().setName(branchName).call();
	}

	@Test
	public void チェックアウトしてpullする() throws IOException, GitAPIException {
		Git git = Git.open(new File(dir));

		String branchName = "test";
		String prefix = "refs/heads";
		String fullName = prefix + "/" + branchName;

		//ブランチが存在するかどうか確認
		boolean branchExists = false;
		for (Ref ref : git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call()) {
			System.out.println(ref.getName());
			if (fullName.equals(ref.getName())) {
				branchExists = true; break;
			}
		}

		//ブランチが存在しなければ作成
		if (!branchExists) {
			git.branchCreate()
					.setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)
					.setStartPoint("origin/" + branchName)
					.setName(branchName)
					.call();
		}

		//チェックアウト & プル
		git.checkout().setName(branchName).call();
		git.pull().setCredentialsProvider(cred()).call();
	}

	@Test
	public void ファイルを作成してコミットする() throws IOException, GitAPIException {
		Git git = Git.open(new File(dir));

		//コミットするためのファイルを作成する
		File f = new File(dir + "/hoge.txt");
		try (PrintWriter pw = new PrintWriter(new FileOutputStream(f))) {
			pw.append(new Date().toString());
		}

		//コミッター情報、及びコミットメッセージを設定する
		PersonIdent person = new PersonIdent("hoge", "hoge@hoge.local");
		String msg = "コミットコメントです。";

		//git add → git commit
		git.add().addFilepattern(".").call();
		git.commit().setAuthor(person).setCommitter(person).setMessage(msg).call();

	}

	@Test
	public void 同名でブランチをpushする() throws IOException, GitAPIException {
		Git git = Git.open(new File(dir));
		Ref ref = git.getRepository().getRef("test"); //ローカルに存在するブランチ名
		git.push().setCredentialsProvider(cred())
				.setRemote("origin")
				.add(ref)
				.call();
	}

	@Test
	public void 異なる名前でブランチをpushする() throws IOException, GitAPIException {
		RefSpec spec = new RefSpec("test2:sample2"); //ローカルブランチ名:リモートブランチ名
		Git.open(new File(dir))
				.push().setCredentialsProvider(cred())
				.setRemote("origin")
				.setRefSpecs(spec)
				.call();
	}

	private CredentialsProvider cred() {
		return new UsernamePasswordCredentialsProvider(user, pass);
	}
}
