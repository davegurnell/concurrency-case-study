package code

import java.util.concurrent.{ExecutorService, Executors}

import cats.effect.{ContextShift, IO, Resource, Timer}
import cats.implicits._
import code.FutureMain.{runExample, veryParallelExample, verySequentialExample}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

object CatsIOMain {

  import Helpers._

  val eagerExample =
    IO.pure(work("A"))

  val lazyExample =
    IO(work("B"))

  def sleepExample(implicit timer: Timer[IO]) =
    logF("C") {
      for {
        _ <- IO.sleep(1.second)
        a <- IO(work("D"))
      } yield a
    }

  def sequentialExample = {
    logF("F") {
      for {
        a <- IO(work("G"))
        b <- IO(work("H"))
      } yield (a, b)
    }
  }

  def parallelExample(implicit cs: ContextShift[IO]) = {
    logF("I") {
      for {
        fa <- IO(work("J")).start(cs)
        fb <- IO(work("K")).start(cs)
        a <- fa.join
        b <- fb.join
      } yield (a, b)
    }
  }

  def verySequentialExample =
    (1 to 20).toList.traverse(i => IO(work(i)))

  def veryParallelExample(implicit cs: ContextShift[IO]) =
    (1 to 20).toList.parTraverse(i => IO(work(i)))

  def raceExample(implicit cs: ContextShift[IO]) =
    IO.race(
      IO(work("H1")),
      IO(work("I1")) *> IO(work("I2")) *> IO.cancelBoundary *> IO(work("I3"))
    )

  def resource: Resource[IO, Int] =
    Resource.make {
      IO {
        val resource = (Math.random * 1000).toInt
        println("Acquiring resource: " + resource)
        resource
      }
    } { resource =>
      IO(println("Releasing resource: " + resource))
    }

  def happyPathExample =
    resource.use { resource =>
      IO(println("Using resource: " + resource)) *> IO(work("J"))
    }

  def unhappyPathExample =
    resource.use { resource =>
      IO(println("Using resource: " + resource)) *> IO(???)
    }

  def runExample(numThreads: Int)(func: ContextShift[IO] => IO[Any]) = {
    val service: ExecutorService =
      Executors.newFixedThreadPool(numThreads)

    val shift: ContextShift[IO] =
      IO.contextShift(ExecutionContext.fromExecutor(service))

    try {
      val ans = func(shift).unsafeRunSync()
      println(s"Final result: $ans")
    } finally {
      service.shutdown()
    }
  }

  def main(args: Array[String]): Unit = {
    println("Number of CPUs: " + Runtime.getRuntime.availableProcessors())

    args(0) match {
      case "eager" => runExample(numThreads = 1)(_ => eagerExample)
      case "lazy" => runExample(numThreads = 1)(_ => lazyExample)
      case "seq1" => runExample(numThreads = 1)(_ => sequentialExample)
      case "seq2" => runExample(numThreads = 2)(_ => sequentialExample)
      case "par1" => runExample(numThreads = 1)(parallelExample(_))
      case "par2" => runExample(numThreads = 2)(parallelExample(_))
      case "vseq1" => runExample(numThreads = 1)(_ => verySequentialExample)
      case "vseq2" => runExample(numThreads = 2)(_ => verySequentialExample)
      case "vpar1" => runExample(numThreads = 1)(veryParallelExample(_))
      case "vpar2" => runExample(numThreads = 2)(veryParallelExample(_))
      case "vpar4" => runExample(numThreads = 4)(veryParallelExample(_))
      case "vpar8" => runExample(numThreads = 8)(veryParallelExample(_))
      case "race" => runExample(numThreads = 2)(raceExample(_))
      case "happy" => runExample(numThreads = 2)(_ => happyPathExample)
      case "unhappy" => runExample(numThreads = 2)(_ => unhappyPathExample)
    }
  }
}
