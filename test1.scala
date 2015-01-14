package jp.synm.mkn
package command
package root

import org.eclipse.jgit.api._
import org.eclipse.jgit.transport.RefSpec
import git.Repo._
import ui.UI
import Tap._


object Init {
  def apply(repositoryUrl: String, directoryPath: java.io.File) = {
    val username = UI.input("username")
    val password = UI.inputPassword("password")

    val url = authenticatedUrl(repositoryUrl, username, password)

    new CloneCommand().setURI(url).setDirectory(directoryPath).call()

    UI.message("MKN Project has been created!")
    UI.message(s"	$$ cd ${directoryPath}")
  }

  private def authenticatedUrl(originalUrl: String, username: String, password: String): String = {
    import java.net.URL
    import java.net.URLEncoder
    val encoding = "UTF-8"

    val url = new URL(originalUrl)
    val portText = if (url.getPort >= 0) s":${url.getPort}" else ""
    s"${url.getProtocol}://${URLEncoder.encode(username, encoding)}:${URLEncoder.encode(password, encoding)}@${url.getHost}${portText}${url.getPath}"
  }
}


object Sync {
  def apply() = {
    repo.errorIfDirty()

    UI.message("サーバと同期しています...")
    repo.synchronizeAllBranches()
    UI.message("サーバと同期しました。")
  }
}


object Add {
  def apply(filePatterns: Seq[String]) = {
    repo.raw.add().tap { cmd => filePatterns.foreach(cmd.addFilepattern(_)) } .call()
    UI.message("ファイルが追加されました。")
    root.Status()
  }
}


object Save {
  def apply(message: Option[String]) = {
    import Tap._

    repo.errorIfClean()
    // git commit --all
    repo.raw.commit.setAll(true).setMessage(message getOrElse "No Message").call()
    UI.message("変更が保存されました")
    Status()
    // TODO なにがコミットされたか表示したいが…
  }
}


object Status {
  def apply() = {
    repo.showStatus()
  }
}


object Reset {
  def apply() = {
    import org.eclipse.jgit.api._

    repo.errorIfClean()
    repo.confirmChangesElimination {
      repo.raw.reset.setRef("HEAD").setMode(ResetCommand.ResetType.HARD).call()
      UI.message("変更を取り消した。")
    }
  }
}


object Remove {
  def apply(patterns: Seq[String]) = {
    import org.eclipse.jgit.api._

    repo.raw.rm.tap { cmd =>
      patterns.foreach(cmd.addFilepattern(_))
    }.setCached(true).call()
    UI.message("ファイルを管理対象外にしました。")
    root.Status()
  }
}
// appended from testAdd
// appended from testAdd
// appended from testSync
// appended from testAdd
// appended from testAdd
// appended from testAdd
// appended from testSync
// appended from testAdd
// appended from testSync
// appended from testAdd
// appended from testAdd
// appended from testSync
// appended from testAdd
// appended from testAdd
// appended from testSync
// appended from testAdd
// appended from testAdd
