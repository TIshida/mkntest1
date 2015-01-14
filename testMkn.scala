package jp.synm.mkn


import org.rogach.scallop._
import git.Repo._
import ui.UI


class MknConf(arguments: Seq[String]) extends ScallopConf(arguments) {
  printedName = "mkn"
  version("mkn 0.0.1 (c) Synergy Marketing")
  banner("""Usage: mkn [OPTION].... [foo|bar] [OPTION].... [foo|bar]
            |mkn is......[describe the app here]
            |""".stripMargin)
  footer("\n for all other information, see [url]")


  val init = new Subcommand("init") {
    descr("Initialize and clone repository to start")

    val repositoryUrl = trailArg[String](descr = "リモートリポジトリの URL")
    val localDirectory = trailArg[String](descr = "作業用のローカルディレクトリ")
  }

  val sync = new Subcommand("sync") {
    descr("サーバと同期する")
  }

  val status = new Subcommand("status") {
    descr("Current repository status")
  }

  val add = new Subcommand("add") {
    descr("指定のファイルを管理対象に追加します")

    val patterns = trailArg[List[String]](descr = "追加するファイル")
  }

  val remove = new Subcommand("remove") {
    descr("指定のファイルを管理対象から削除します")

    val patterns = trailArg[List[String]](descr = "管理対象外にするファイル")
  }

  val save = new Subcommand("save") {
    descr("現在のデータを保存")

    val message = trailArg[String](descr = "コメント", required = false)
  }

  val reset = new Subcommand("reset") {
    descr("変更を取り消し最後に保存した状態に戻す")
  }

  val tri = new Subcommand("try") {
    descr("トライ関係のコマンド")

    val new_ = new Subcommand("new") {
      descr("新たにトライを始める")

      val name = trailArg[String](descr = "トライの名前")
    }

    val switch = new Subcommand("switch") {
      descr("保存済みの別のトライに切りかえる")

      val name = trailArg[String](descr = "トライの名前")
    }

    val ok = new Subcommand("ok") {
      descr("パターンを保存してマージする")

      val name = trailArg[String](descr = "パターンの名前")
      val message = trailArg[String](descr = "パターンに対するコメントなど", required = false)
    }
  }

  val pattern = new Subcommand("pattern") {
    descr("TRY/PATTERN 関係のコマンド")

    val new_ = new Subcommand("new") {
      descr("新たにパターンを始める")

      val name = trailArg[String](descr = "パターンの名前")
    }

    val switch = new Subcommand("switch") {
      descr("保存済みの別のパターンに切りかえる")

      val name = trailArg[String](descr = "パターンの名前")
    }
  }
}


object Mkn {
  def main(args: Array[String]) {
    import org.fusesource.jansi.AnsiConsole

    AnsiConsole.systemInstall()

    try {
      if (args.isEmpty)
        new MknConf(Nil).printHelp()
      else
        _main(args)
    } catch {
      case e: exception.MKNError =>
        UI.error(Option(e.getMessage) getOrElse e.toString)
      case e: Throwable =>
        e.printStackTrace()
        UI.error(Option(e.getMessage) getOrElse e.toString)
        new MknConf(Nil).printHelp()
    }

    AnsiConsole.systemUninstall()
  }

  private def _main(args: Seq[String]) = {
    val conf = new MknConf(args)

    val last = conf.subcommands.last
    if (last == conf.init) {
      command.root.Init(
        conf.init.repositoryUrl,
        new java.io.File(conf.init.localDirectory))
    } else {
      repo.check()
      last match {
        // root
        case conf.status => command.root.Status()
        case conf.sync => command.root.Sync()
        case conf.add => command.root.Add(conf.add.patterns)
        case conf.remove => command.root.Remove(conf.remove.patterns)
        case conf.save => {
          command.root.Save(conf.save.message.get)
          command.root.Sync()
        }
        case conf.reset => command.root.Reset()
        // try
        case conf.tri.new_ => {
          command.tri.New(conf.tri.new_.name)
          command.root.Sync()
        }
        case conf.tri.switch => command.tri.Switch(conf.tri.switch.name)
        case conf.tri.ok => {
          command.tri.Ok(conf.tri.ok.name, conf.tri.ok.message.get)
          command.root.Sync()
        }
        // pattern
        case conf.pattern.new_ => {
          command.pattern.New(conf.pattern.new_.name)
          command.root.Sync()
        }
        case conf.pattern.switch => command.pattern.Switch(conf.pattern.switch.name)
        case _ => UI.error(s"Unknow sub command: ${args.headOption}")
      }
    }
  }

  // イチイチ conf.foo.get.get とするのは見苦しいので…
  private implicit def scallopOpt2content[T](from: org.rogach.scallop.ScallopOption[T]): T = from.get.get
}

// appended from testAdd
// appended from testAdd
// appended from testSync
// appended from testAdd
