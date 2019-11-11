package code

import java.util.concurrent.{ExecutorService, Executors}

import cats.effect.{ContextShift, IO, Resource, Timer}
import cats.implicits._
import code.MonixMain.{mapReduceExample, runExample}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object CatsIOMain {

  import Helpers._

  /** Call work() immediately. Don't wait. */
  val eagerExample =
    ???

  /** Call work() work lazily. Wait until needed. */
  val lazyExample =
    ???

  /** Call work() twice in sequence. Collect a tuple of the results. */
  def sequentialExample =
    ???

  /** Call work() twice in parallel. Collect a tuple of the results. */
  def parallelExample(implicit cs: ContextShift[IO]) =
    ???

  /** Call work() 20 times in sequence. Collect a list of the results. */
  def verySequentialExample =
    ???

  /** Call work() 20 times in parallel. Collect a list of the results. */
  def veryParallelExample(implicit cs: ContextShift[IO]) =
    ???

  /** Sleep for one second then call work(). Don't block! */
  def sleepExample(implicit timer: Timer[IO]) =
    ???

  /** Call work() twice in parallel. Return the first result to complete. */
  def raceExample(implicit cs: ContextShift[IO]) =
    ???

  /** Helper function for use in happyPathExample and unhappyPathExample */
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

  /**
   * Call acquire(), do work, then call release().
   * Pass the acquired resource to release.
   * Your code for this and unhappyPathExample should be the same.
   */
  def happyPathExample =
    ???

  /**
   * Call acquire(), throw an exception, then call release().
   * Pass the acquired resource to release.
   * Your code for this and happyPathExample should be the same.
   */
  def unhappyPathExample =
    ???

  /**
   * Calculate the first FIB_MAX fibonacci numbers in parallel
   * and add up all the results.
   */
  def mapReduceExample(implicit cs: ContextShift[IO]) =
    ???

  def runExample(numThreads: Int)(func: (Timer[IO], ContextShift[IO]) => IO[Any]) = {
    val service: ExecutorService =
      Executors.newFixedThreadPool(numThreads)

    val context: ExecutionContext =
      ExecutionContext.fromExecutor(service)

    val shift: ContextShift[IO] =
      IO.contextShift(context)

    val timer: Timer[IO] =
      IO.timer(context)

    try {
      val ans = func(timer, shift).unsafeRunSync()
      println(s"Final result: $ans")
    } finally {
      service.shutdown()
    }
  }

  def main(args: Array[String]): Unit = {
    println("Number of CPUs: " + Runtime.getRuntime.availableProcessors())

    args(0) match {
      case "eager" => runExample(numThreads = 1)((t, c) => eagerExample)
      case "lazy" => runExample(numThreads = 1)((t, c) => lazyExample)
      case "seq1" => runExample(numThreads = 1)((t, c) => sequentialExample)
      case "seq2" => runExample(numThreads = 2)((t, c) => sequentialExample)
      case "par1" => runExample(numThreads = 1)((t, c) => parallelExample(c))
      case "par2" => runExample(numThreads = 2)((t, c) => parallelExample(c))
      case "vseq1" => runExample(numThreads = 1)((t, c) => verySequentialExample)
      case "vseq2" => runExample(numThreads = 2)((t, c) => verySequentialExample)
      case "vpar1" => runExample(numThreads = 1)((t, c) => veryParallelExample(c))
      case "vpar2" => runExample(numThreads = 2)((t, c) => veryParallelExample(c))
      case "vpar4" => runExample(numThreads = 4)((t, c) => veryParallelExample(c))
      case "vpar8" => runExample(numThreads = 8)((t, c) => veryParallelExample(c))
      case "sleep" => runExample(numThreads = 2)((t, c) => sleepExample(t))
      case "race" => runExample(numThreads = 2)((t, c) => raceExample(c))
      case "happy" => runExample(numThreads = 2)((t, c) => happyPathExample)
      case "unhappy" => runExample(numThreads = 2)((t, c) => unhappyPathExample)
      case "mapreduce" => runExample(numThreads = 8)((t, c) => mapReduceExample(c))
    }
  }
}
