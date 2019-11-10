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

  val eagerExample =
    Task.now(work("A"))

  val lazyExample =
    Task.eval(work("B"))

  def sleepExample(implicit timer: Timer[Task]) =
    logF("C") {
      for {
        _ <- Task.sleep(1.second)
        a <- Task.eval(work("D"))
      } yield a
    }

  def sequentialExample = {
    logF("F") {
      for {
        a <- Task.eval(work("G"))
        b <- Task.eval(work("H"))
      } yield (a, b)
    }
  }

  def parallelExample = {
    logF("I") {
      for {
        a <- Task.evalAsync(work("J"))
        b <- Task.evalAsync(work("K"))
      } yield (a, b)
    }
  }

  def verySequentialExample =
    (1 to 20).toList.traverse(i => Task(work(i)))

  def veryParallelExample =
    (1 to 20).toList.parTraverse(i => Task(work(i)))

  def raceExample =
    Task.race(
      Task(work("H1")),
      Task(work("I1")) *> Task(work("I2")) *> Task.cancelBoundary *> Task(work("I3"))
    )

  def resource: Resource[Task, Int] =
    Resource.make {
      Task {
        val resource = (Math.random * 1000).toInt
        println("Acquiring resource: " + resource)
        resource
      }
    } { resource =>
      Task(println("Releasing resource: " + resource))
    }

  def happyPathExample =
    resource.use { resource =>
      Task(println("Using resource: " + resource))
    }

  def unhappyPathExample =
    resource.use { resource =>
      Task(???)
    }

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
      case "race" => runExample(numThreads = 8)(raceExample)
      case "happy" => runExample(numThreads = 8)(happyPathExample)
      case "unhappy" => runExample(numThreads = 8)(unhappyPathExample)
    }
  }
}
