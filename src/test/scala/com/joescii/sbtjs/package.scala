package com.joescii

import java.io.{InputStreamReader, BufferedReader, File}

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

package object sbtjs {
  type Result = Future[(Int, List[String])]

  val separator = System.getProperty("file.separator")
  val windows = System.getProperty("os.name").matches("(?i).*win.*")
  val cd = new File(".")

  implicit class EnhancedFile(val f:File) extends AnyVal {
    def /(child:String):File = new File(f, child)
  }

  implicit class EnhancedString(val s:String) extends AnyVal {
    def /(child:String):String = s + separator + child
  }

  def runSbt(dir:File, tasks:String*):Result = Future {
    val sbtBin = System.getenv("SBT_HOME") / "sbt" + (if(windows) ".bat" else "")
    val cmd = sbtBin :: tasks.toList
    val builder = new ProcessBuilder(cmd:_*)
    builder.directory(dir)
    builder.redirectErrorStream(true)

    println(s"Running ${cmd.mkString(" ")} in $dir...")

    val process = builder.start()
    val stream = process.getInputStream
    val buffer = new BufferedReader(new InputStreamReader(stream))
    lazy val status = process.waitFor()
    lazy val output = Iterator.continually(buffer.readLine())
      .takeWhile(_ != null)
      .dropWhile(!_.startsWith("[info] Set current project to")) // Drop all of the usual junk
      .drop(1) // then drop the last line of usual junk
      .toList

    (status, output)
  }

  trait SbtJsTestSpec extends WordSpec with ShouldMatchers with ScalaFutures {
    implicit val defaultPatience = PatienceConfig(timeout = Span(60, Seconds), interval = Span(500, Millis))

    var result:Result = Future.failed(new Exception)
  }
}