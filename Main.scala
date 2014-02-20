import scala.collection.convert.wrapAll._
import org.eclipse.egit.github.core, core.Repository, core.service._, core.client._
import scala.tools.jline.console.ConsoleReader

object Main extends App {

  val org = args(0)

  val logins = args.drop(1).toSet

  val apiToken = new ConsoleReader().readLine("GitHub API Token >", '*')

  val repoService = new RepositoryService(new GitHubClient().setOAuth2Token(apiToken))

  val allRepos = repoService.getOrgRepositories(org)

  println(s"Searching ${allRepos.size} repos for contributions by ${logins.mkString(", ")}")

  def contributorLogins(r: Repository) = repoService.getContributors(r, false).map(_.getLogin).toSet

  val reposWithContributionsBySpecifiedUsers = allRepos.par.map(repo => repo.generateId -> (contributorLogins(repo) intersect logins)).toMap.filter(_._2.nonEmpty)

  for ((r, users) <- reposWithContributionsBySpecifiedUsers) println(s"$r : ${users.mkString(",")}")

}
