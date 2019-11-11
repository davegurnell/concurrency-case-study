package code

import java.util.concurrent.{ExecutorService, Executors}

import cats.effect.{Resource, Timer}
import cats.implicits._
import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.Await
import scala.concurrent.duration._

object MonixMain {

  // Useful Monix documentation:
  // https://monix.io/docs/3x/eval/task.html#introduction

  import Helpers._

  /** Call work() immediately. Don't wait. */
  val eagerExample =
    ???

  /** Call work() work lazily. Wait until needed. */
  val lazyExample =
    ???

  /** Call work() twice in sequence. Collect a tuple of the results. */
  val sequentialExample =
    ???

  /** Call work() twice in parallel. Collect a tuple of the results. */
  val parallelExample =
    ???

  /** Call work() twice in parallel. Collect a tuple of the results. */
  val verySequentialExample =
    ???

  /** Call work() 20 times in parallel. Collect a list of the results. */
  val veryParallelExample =
    ???

  /** Sleep for one second then call work(). Don't block! */
  val sleepExample =
    ???

  /** Call work() twice in parallel. Return the first result to complete. */
  val raceExample =
    ???

  val resource: Resource[Task, Int] =
    Resource.make {
      Task {
        val resource = (Math.random * 1000).toInt
        println("Acquiring resource: " + resource)
        resource
      }
    } { resource =>
      Task(println("Releasing resource: " + resource))
    }

  /**
   * Call acquire(), do work, then call release().
   * Pass the acquired resource to release.
   * Your code for this and unhappyPathExample should be the same.
   */
  val happyPathExample =
    ???

  /**
   * Call acquire(), throw an exception, then call release().
   * Pass the acquired resource to release.
   * Your code for this and happyPathExample should be the same.
   */
  val unhappyPathExample =
    ???

  /**
   * Calculate the first FIB_MAX fibonacci numbers in parallel
   * and add up all the results.
   */
  val mapReduceExample =
    ???

  def runExample(numThreads: Int)(task: => Task[Any]): Unit = {
    val service: ExecutorService =
      Executors.newFixedThreadPool(numThreads)

    val scheduler: Scheduler =
      Scheduler(service)

    try {
      val ans = Await.result(task.runToFuture(scheduler), Duration.Inf)
      println(s"Final result: $ans")
    } finally {
      service.shutdown()
    }
  }

  def main(args: Array[String]): Unit = {
    println("Number of CPUs: " + Runtime.getRuntime.availableProcessors())

    args(0) match {
      case "eager" => runExample(numThreads = 1)(eagerExample)
      case "lazy" => runExample(numThreads = 1)(lazyExample)
      case "seq1" => runExample(numThreads = 1)(sequentialExample)
      case "seq2" => runExample(numThreads = 2)(sequentialExample)
      case "par1" => runExample(numThreads = 1)(parallelExample)
      case "par2" => runExample(numThreads = 2)(parallelExample)
      case "vseq1" => runExample(numThreads = 1)(verySequentialExample)
      case "vseq2" => runExample(numThreads = 2)(verySequentialExample)
      case "vpar1" => runExample(numThreads = 1)(veryParallelExample)
      case "vpar2" => runExample(numThreads = 2)(veryParallelExample)
      case "vpar4" => runExample(numThreads = 4)(veryParallelExample)
      case "vpar8" => runExample(numThreads = 8)(veryParallelExample)
      case "race" => runExample(numThreads = 2)(raceExample)
      case "happy" => runExample(numThreads = 2)(happyPathExample)
      case "unhappy" => runExample(numThreads = 2)(unhappyPathExample)
      case "mapreduce" => runExample(numThreads = 8)(mapReduceExample)
    }
  }
}
