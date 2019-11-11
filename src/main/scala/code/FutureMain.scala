package code

import java.util.concurrent.{ExecutorService, Executors}

import cats.effect.{ContextShift, IO}
import cats.implicits._

import scala.concurrent.duration._
import scala.concurrent.{Future, _}

object FutureMain {

  import Helpers._

  /** Call work() immediately. Don't wait. */
  def eagerExample(implicit ec: ExecutionContext) =
    ???

  /** Call work() work lazily. Wait until needed. */
  def lazyExample(implicit ec: ExecutionContext) =
    ???

  /** Call work() twice in sequence. Collect a tuple of the results. */
  def sequentialExample(implicit ec: ExecutionContext) =
    ???

  /** Call work() twice in parallel. Collect a tuple of the results. */
  def parallelExample(implicit ec: ExecutionContext) =
    ???

  /** Call work() 20 times in sequence. Collect a list of the results. */
  def verySequentialExample(implicit ec: ExecutionContext) =
    ???

  /** Call work() 20 times in parallel. Collect a list of the results. */
  def veryParallelExample(implicit ec: ExecutionContext) =
    ???

  /** Sleep for one second then call work(). Don't block! */
  def sleepExample(implicit ec: ExecutionContext) =
    ???

  /** Call work() twice in parallel. Return the first result to complete. */
  def raceExample(implicit ec: ExecutionContext) =
    ???

  /** Helper function for use in happyPathExample and unhappyPathExample */
  def acquire(implicit ec: ExecutionContext): Future[Int] =
    Future {
      val resource = (Math.random * 1000).toInt
      println("Acquiring resource: " + resource)
      resource
    }

  /** Helper function for use in happyPathExample and unhappyPathExample */
  def release(resource: Int)(implicit ec: ExecutionContext): Future[Unit] =
    Future {
      println("Releasing resource: " + resource)
    }

  /**
   * Call acquire(), do work, then call release().
   * Pass the acquired resource to release.
   * Your code for this and unhappyPathExample should be the same.
   */
  def happyPathExample(implicit ec: ExecutionContext) =
    ???

  /**
   * Call acquire(), throw an exception, then call release().
   * Pass the acquired resource to release.
   * Your code for this and happyPathExample should be the same.
   */
  def unhappyPathExample(implicit ec: ExecutionContext) =
    ???

  /**
   * Calculate the first FIB_MAX fibonacci numbers in parallel
   * and add up all the results.
   */
  def mapReduceExample(implicit ec: ExecutionContext) =
    ???

  def runExample(numThreads: Int)(func: ExecutionContext => Future[Any]): Unit = {
    val service: ExecutorService =
      Executors.newFixedThreadPool(numThreads)

    val context: ExecutionContext =
      ExecutionContext.fromExecutor(service)

    try {
      val ans = Await.result(func(context), Duration.Inf)
      println(s"Final result: $ans")
    } finally {
      service.shutdown()
    }
  }

  def main(args: Array[String]): Unit = {
    println("Number of CPUs: " + Runtime.getRuntime.availableProcessors())

    args(0) match {
      case "eager" => runExample(numThreads = 1)(eagerExample(_))
      case "lazy" => runExample(numThreads = 1)(lazyExample(_))
      case "seq1" => runExample(numThreads = 1)(sequentialExample(_))
      case "seq2" => runExample(numThreads = 2)(sequentialExample(_))
      case "par1" => runExample(numThreads = 1)(parallelExample(_))
      case "par2" => runExample(numThreads = 2)(parallelExample(_))
      case "vseq1" => runExample(numThreads = 1)(verySequentialExample(_))
      case "vseq2" => runExample(numThreads = 2)(verySequentialExample(_))
      case "vpar1" => runExample(numThreads = 1)(veryParallelExample(_))
      case "vpar2" => runExample(numThreads = 2)(veryParallelExample(_))
      case "vpar4" => runExample(numThreads = 4)(veryParallelExample(_))
      case "vpar8" => runExample(numThreads = 8)(veryParallelExample(_))
      case "race" => runExample(numThreads = 2)(raceExample(_))
      case "happy" => runExample(numThreads = 2)(happyPathExample(_))
      case "unhappy" => runExample(numThreads = 2)(unhappyPathExample(_))
      case "mapreduce" => runExample(numThreads = 8)(mapReduceExample(_))
    }
  }
}
